package com.giovanny.flashsaledemo.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.regex.Matcher;

/**
 * @packageName: common
 * @className: BaseEntity
 * @description:
 * @author: YangJun
 * @date: 2020/5/14 17:47
 * @version: v1.0
 **/
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;


    public static void main(String[] args) {
        String regex = "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";
//        String regex = "^([01]\\d|2[0-3]):[0-5]\\d$";
        String time = "01:59:59";
        boolean matches = time.matches(regex);
        System.out.println(matches);
    }
}
