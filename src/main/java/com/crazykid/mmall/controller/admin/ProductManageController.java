package com.crazykid.mmall.controller.admin;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.Product;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IFileService;
import com.crazykid.mmall.service.IProductService;
import com.crazykid.mmall.service.IUserService;
import com.crazykid.mmall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/admin/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            return iProductService.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            return iProductService.managerProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            return iProductService.getProductList(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam MultipartFile upload_file, HttpServletRequest request, HttpSession session) {
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员");
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            //通过上下文获取发布目录的路径 即webapp下
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(upload_file,path);
            String url = PropertiesUtil.getProperties("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("无权限");
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam MultipartFile upload_file, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Map resultMap = Maps.newHashMap();
        //判断用户是否已登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //判断用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //实现逻辑...
            //前端使用simditor富文本编辑器，所以要按照它的要求去做返回
            //https://simditor.tower.im/docs/doc-config.html#anchor-upload
            /*
                JSON response after uploading complete:
                {
                  "success": true/false,
                  "msg": "error message", # optional
                  "file_path": "[real file path]"
                }
             */

            //通过上下文获取发布目录的路径 即webapp下
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(upload_file,path);
            if (StringUtils.isEmpty(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperties("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        }
        resultMap.put("success", false);
        resultMap.put("msg", "无权限操作");
        return resultMap;
    }
}
