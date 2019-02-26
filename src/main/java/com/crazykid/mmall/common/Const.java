package com.crazykid.mmall.common;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "current_user";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    //商品排序
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    //购物车checked状态和是否超出库存的标记
    public interface Cart {
        int CHECKED = 1; //选中状态
        int UN_CHECKED = 0; //非选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL"; //限制失败
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS"; //限制成功
    }

    //用户组
    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    //商品上架状态
    @Getter
    @AllArgsConstructor
    public enum ProductStatusEnum {
        ON_SALE(1,"在线");

        private int code;
        private String value;
    }

    //支付宝订单状态
    @Getter
    @AllArgsConstructor
    public enum OrderStatusEnum {
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");

        private int code;
        private String value;

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    //支付宝回调 通知触发条件
    public interface AlipayCallBack {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
        //String TRADE_STATUS_TRADE_FINISHED = "TRADE_FINISHED";
        //String TRADE_STATUS_TRADE_CLOSED = "TRADE_CLOSED";

        String RESPONSE_SUCCESS = "success"; //支付宝规定回调验证正确必须返回success
        String RESPONSE_FAILED = "failed"; //返回其他任意都算失败 支付宝会重复发回调
    }

    //支付平台
    @Getter
    @AllArgsConstructor
    public enum PayPlantformEnum {
        ALIPAY(1,"支付宝");
        //WECHAT(2,"微信");

        private int code;
        private String value;
    }

    //支付方式
    @Getter
    @AllArgsConstructor
    public enum PaymentTypeEnum {
        ONLINE_PAY(1,"在线支付");

        private int code;
        private String value;

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
