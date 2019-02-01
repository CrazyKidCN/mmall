package com.crazykid.mmall.controller.portal;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/") //放在类上 代表统一访问前缀
public class UserController {

    private final IUserService iUserService;

    @Autowired
    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }


    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param session session
     * @return 响应内容
     */
    @PostMapping(value = "login.do") //限定post方式访问
    @ResponseBody //返回json
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param session session
     * @return 响应内容
     */
    @GetMapping(value = "logout.do") //限定get方式访问
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER); //移除session
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user 表单内容序列化成user对象
     * @return 响应内容 成功/失败
     */
    @PostMapping(value = "register.do")
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验用户名或邮箱是否已存在
     * @param str 用户名或邮箱
     * @param type username或email
     * @return success表示不存在 否则反之
     */
    @GetMapping(value = "check_valid.do")
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }
}
