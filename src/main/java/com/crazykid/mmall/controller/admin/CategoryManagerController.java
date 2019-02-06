package com.crazykid.mmall.controller.admin;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.ICategoryService;
import com.crazykid.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/category")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(defaultValue = "0") int parentId) {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(defaultValue = "0") Integer categoryId) {
        //查询子节点的category信息，并且不递归，保持平级
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(defaultValue = "0") Integer categoryId) {
        //查询当前节点的ID 和递归子节点的ID
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }
}
