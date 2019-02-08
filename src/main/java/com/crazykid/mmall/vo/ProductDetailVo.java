package com.crazykid.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDetailVo {
    private Integer id;
    private Integer categoryId; //商品品类ID
    private String name; //商品名称
    private String subtitle; //副标题
    private String mainImage; //主图片
    private String subImages; //子图片
    private String detail; //详细信息(富文本)
    private BigDecimal price; //价格
    private Integer stock; //库存
    private Integer status; //状态
    private String createTime; //创建时间
    private String updateTime; //最后更新时间

    private String imageHost; //图片的服务器的URL前缀
    private Integer parentCategoryId; //父品类的ID
}
