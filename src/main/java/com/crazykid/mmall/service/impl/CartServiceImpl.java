package com.crazykid.mmall.service.impl;

import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ResponseCode;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.dao.CartMapper;
import com.crazykid.mmall.dao.ProductMapper;
import com.crazykid.mmall.pojo.Cart;
import com.crazykid.mmall.pojo.Product;
import com.crazykid.mmall.service.ICartService;
import com.crazykid.mmall.util.BigDecimalUtil;
import com.crazykid.mmall.util.PropertiesUtil;
import com.crazykid.mmall.vo.CartProductVo;
import com.crazykid.mmall.vo.CartVo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public class CartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        //空参数判断
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //检查用户是否已经添加过该商品
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart==null) {
            //购物车里没有该商品，新增之
            Cart cartItem = new Cart();
            cartItem.setQuantity(count); //数量就是加入购物车的数量
            cartItem.setChecked(Const.Cart.CHECKED); //默认是选中状态
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        } else {
            //已经在购物车里，数量相加
            Cart cartItem = new Cart();
            count += cart.getQuantity();
            cartItem.setQuantity(count);
            cartItem.setId(cart.getId());
            cartMapper.updateByPrimaryKeySelective(cartItem);
        }
        CartVo cartVo = this.assmbleCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //
    private CartVo assmbleCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId); //获取该用户的购物车
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0"); //初始化总价是0

        if(!CollectionUtils.isEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId()); //获取商品的信息
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount;
                    if (product.getStock() >= cartItem.getQuantity()) { //如果库存充足
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        buyLimitCount = cartItem.getQuantity(); //购买数就是添加进购物车的数
                    } else { //库存不足
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        buyLimitCount = product.getStock(); //购买数就是商品库存数
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) { //如果勾选，计算到总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperties("ftp.server.http.prefix"));
        return cartVo;
    }

    //是否全选状态
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        //如果用户未选中的商品数量是0，那就是全选了
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
