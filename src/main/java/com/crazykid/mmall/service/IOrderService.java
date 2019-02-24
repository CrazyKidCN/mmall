package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
    ServerResponse alipayCallback(Map<String,String> params);
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
