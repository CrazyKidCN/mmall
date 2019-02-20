package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse del(Integer userId, Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
