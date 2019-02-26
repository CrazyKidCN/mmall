package com.crazykid.mmall.controller.admin;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IOrderService;
import com.crazykid.mmall.service.IUserService;
import com.crazykid.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> detail(HttpSession session, Long orderNo) {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iOrderService.manageDetail(orderNo);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("search.do")
    public ServerResponse<PageInfo>managerSearch(HttpSession session, Long orderNo , @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize)  {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }

    @RequestMapping("send_goods.do")
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {
        //判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //判断用户的角色
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //是管理员
            return iOrderService.manageSendGoods(orderNo);
        } else {
            //不是管理员
            return ServerResponse.createByErrorMessage("无权限");
        }
    }
}
