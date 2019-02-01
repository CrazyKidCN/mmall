package com.crazykid.mmall.controller.portal;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    @PostMapping //限定post方式访问
    @ResponseBody //返回json
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }
}
