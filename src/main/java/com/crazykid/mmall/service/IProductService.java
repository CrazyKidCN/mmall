package com.crazykid.mmall.service;

import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.pojo.Product;
import com.crazykid.mmall.vo.ProductDetailVo;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus(Integer productId, Integer status);
    ServerResponse<ProductDetailVo> managerProductDetail(Integer productId);
}
