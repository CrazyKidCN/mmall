package com.crazykid.mmall.service.impl;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.common.TokenCache;
import com.crazykid.mmall.dao.UserMapper;
import com.crazykid.mmall.pojo.User;
import com.crazykid.mmall.service.IUserService;
import com.crazykid.mmall.util.MD5Util;
import com.mysql.fabric.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.crazykid.mmall.common.TokenCache.TOKEN_PREFIX;

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
        String password_md5 = MD5Util.Encode(password);

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
        String password_md5 = MD5Util.Encode(user.getPassword());
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

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);

        if (!StringUtils.isEmpty(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //有返回结果说明问题和答案是这个用户的 而且是正确的
            //生成一个随机token
            String forgetToken = UUID.randomUUID().toString();
            //放进缓存
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        //判断forgetToken是否传入
        if (StringUtils.isEmpty(forgetToken)) {
            return ServerResponse.createByErrorMessage("token为空");
        }
        //判断传入的username是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //从缓存中读token，如果读不出说明已过期
        String token = TokenCache.getKey(TOKEN_PREFIX + username);
        if (StringUtils.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //缓存的token读出来了，看看跟前台传入的token是否一致
        if (forgetToken.equals(token)) {
            //将密码加密成MD5
            String password_md5 = MD5Util.Encode(passwordNew);
            //更新数据库
            int rowCount = userMapper.updatePasswordByUsername(username, password_md5);
            if (rowCount>0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，因为我们会查询一个count(1)，如果不指定id，那么结果就是true count>0
        int resultCount = userMapper.checkPassword(MD5Util.Encode(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        //给user对象赋值新密码  md5加密
        user.setPassword(MD5Util.Encode(passwordNew));

        //更新数据库 并取回影响的行数
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码重置成功");
        }
        return ServerResponse.createByErrorMessage("密码重置失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //email要进行一个校验,数据库中是否已存在该email? 【并且不是自己在用这个email】
        int resultCount = userMapper.checkEmailByUserid(user.getEmail(), user.getId());
        if (resultCount > 0) {
            ServerResponse.createByErrorMessage("email已被他人使用");
        }

        //设置需要更新的字段 【username/role是不能被更新的】
        User updateUser = new User();
        updateUser.setId(user.getId()); //主键
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        //updateByPrimaryKey: 直接把传入的对象的属性全更新进去(即使属性是null)
        //updateByPrimaryKeySelective: 传入的对象哪些属性不为null才设置哪些字段
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", user);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(null);
        return ServerResponse.createBySuccess(user);
    }

    //admin
    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user!=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
