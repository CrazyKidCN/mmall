package com.crazykid.mmall.service.impl;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.dao.UserMapper;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //将密码加密成MD5
        String password_md5 = DigestUtils.md5DigestAsHex(password.getBytes());

        User user = userMapper.selectLogin(username, password_md5);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword("");
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //检查用户名是否重复
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //检查EMAIL是否重复
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //设置用户权限
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //设置用户密码 md5加密
        String password_md5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(password_md5);

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) { //返回的是受影响的行数，为0肯定是出现异常(没有插入成功 之类)
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createByErrorMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (!StringUtils.isEmpty(str)) {
            int resultCount;
            if (Const.USERNAME.equals(type)) {
                resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            } else if (Const.EMAIL.equals(type)) {
                resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("EMAIL已存在");
                }
            } else {
                return ServerResponse.createByErrorMessage("参数有误");
            }
            return ServerResponse.createBySuccess("校验成功");
        } else {
            return ServerResponse.createByErrorMessage("缺少参数 type");
        }
    }
}
