package com.crazykid.mmall.dao;

import com.crazykid.mmall.pojo.Shipping;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByShippingIdUserId(Integer userId, Integer shippingId);

    int updateByShipping(Shipping record);

    Shipping selectByShippingIdUserId(Integer shippingId, Integer userId);

    List<Shipping> selectByUserId(Integer userId);
}