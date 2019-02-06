package com.crazykid.mmall.pojo;

import lombok.*;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id") //只根据id属性
public class Category {
    private Integer id;
    private Integer parentId;
    private String name;
    private Boolean status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
}