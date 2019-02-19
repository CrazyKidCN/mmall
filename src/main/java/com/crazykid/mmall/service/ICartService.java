package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.vo.CartVo;
import org.springframework.stereotype.Service;

@Service("iCartService")
public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> deleteProducts(Integer userid, String productIds);
}
