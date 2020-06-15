package com.giovanny.flashsaledemo.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

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
}
