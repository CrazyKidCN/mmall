package com.crazykid.mmall.controller.admin;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.Product;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IProductService;
import com.crazykid.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

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


}
