package com.crazykid.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.crazykid.mmall.common.Const;
import com.crazykid.mmall.common.ServerResponse;
import com.crazykid.mmall.dao.OrderItemMapper;
import com.crazykid.mmall.dao.OrderMapper;
import com.crazykid.mmall.dao.PayInfoMapper;
import com.crazykid.mmall.pojo.Order;
import com.crazykid.mmall.pojo.OrderItem;
import com.crazykid.mmall.pojo.PayInfo;
import com.crazykid.mmall.service.IOrderService;
import com.crazykid.mmall.util.BigDecimalUtil;
import com.crazykid.mmall.util.DateTimeUtil;
import com.crazykid.mmall.util.FTPUtil;
import com.crazykid.mmall.util.PropertiesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;

    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));



        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //.setNotifyUrl(PropertiesUtil.getProperties("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                //确保存放二维码的路径存在
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/"+"qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                //生成二维码  这是支付宝封装好的
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                //通过FTP上传二维码图片到图片服务器上
                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    log.error("上传支付宝二维码时发生错误", e);
                }
                log.info("qrPath:" + qrPath);

                String qrUrl = PropertiesUtil.getProperties("ftp.server.http.prefix");
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createBySuccessMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createBySuccessMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createBySuccessMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    public ServerResponse alipayCallback(Map<String,String> params) {
        Long outTradeNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        /**
         第五步：需要严格按照如下描述校验通知数据的正确性。
         商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
         并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
         同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方
         （有的时候，一个商户可能有多个seller_id/seller_email），上述有任何一个验证不通过，
         则表明本次通知是异常通知，务必忽略。在上述验证通过后商户必须根据支付宝不同类型的业务通知，
         正确的进行不同的业务处理，并且过滤重复的通知结果数据。在支付宝的业务通知中，
         只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
         */

        //验证该通知数据中的out_trade_no是否为商户系统中创建的订单号
        Order order = orderMapper.selectByOrderNo(outTradeNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("非本商城订单");
        }

        //过滤重复的通知结果数据
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }

        //如果交易成功，就把订单状态设置成已付款
        if (Const.AlipayCallBack.RESPONSE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlantformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
