package com.crazykid.mmall.pojo;

import lombok.*;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PayInfo {
    private Integer id;
    private Integer userId;
    private Long orderNo;
    private Integer payPlatform;
    private String platformNumber;
    private String platformStatus;
    private Date createTime;
    private Date updateTime;
}