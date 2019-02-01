package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
}
