package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
}
