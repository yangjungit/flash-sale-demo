package com.giovanny.flashsaledemo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @packageName: com.giovanny.flashsaledemo.entity.po
 * @className: User
 * @description: //User
 * @author: YangJun
 * @date: 2020/7/1 12:44
 * @version: v1.0
 **/
@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "不能为空")
    private Integer gender;
    private String username;
    private String password;
    private String mail;
    private String address;
    private String birthday;
    private Double height;
    private Double weight;
    private Integer status;
    private String create;
    private Date createTime;
    private String update;
    private Date updateTime;

}
