package com.crazykid.mmall.dao;

import com.crazykid.mmall.pojo.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(Integer userId, Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIdProductIds(Integer userId, List<String> productIdList);
}