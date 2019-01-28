package com.crazykid.mmall.pojo;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Integer checked;
    private Date createTime;
    private Date updateTime;
}