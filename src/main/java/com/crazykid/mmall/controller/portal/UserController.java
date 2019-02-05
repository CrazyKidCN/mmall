package com.crazykid.mmall.controller.portal;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IUserService;
import com.mysql.fabric.Server;
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

    /**
     * 获取当前登录用户的信息
     * @param session session
     * @return 响应内容
     */
    @GetMapping(value = "get_user_info.do")
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     * 获取忘记密码的问题
     * @param username 用户名
     * @return 响应内容
     */
    @GetMapping(value = "forget_get_question.do")
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 检查问题答案
     * @param username 用户名
     * @param question 密码
     * @param answer 答案
     * @return 响应内容
     */
    @PostMapping(value = "forget_check_answer.do")
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 重置密码
     * @param username 用户名
     * @param passwordNew 新密码
     * @param forgetToken 检查问题答案成功之后生成的token
     * @return 响应内容
     */
    @PostMapping(value = "forget_reset_password.do")
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下的重置密码
     * @param session session 用于读出已登录的user对象
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @return 响应内容
     */
    @PostMapping(value = "reset_password.do")
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(user, passwordOld, passwordNew);
    }

    /**
     * 更新用户信息
     * @param session session 用于判断用户是否登录 并且防止横向越权
     * @param user 前台传来的表单信息
     * @return 响应内容
     */
    @PostMapping(value = "update_information.do")
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        //为了防止横向越权，这个user的id和username从已登录用户的session中拿，避免篡改
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        user.setRole(currentUser.getRole());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 获取用户信息
     * @param session session
     * @return 响应内容
     */
    @GetMapping(value = "get_information.do")
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session) {
       User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
       if (currentUser == null) {
           return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录");
       }
       return iUserService.getInformation(currentUser.getId());
    }
}
