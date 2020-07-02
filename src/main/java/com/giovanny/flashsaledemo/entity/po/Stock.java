package com.giovanny.flashsaledemo.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


/**
 * @author YangJun
 * @since 2020-06-15
 */
@Data
public class Stock{

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 名字
     */
    private String name;

    /**
     * 库存
     */
    private Long stock;

    private String version;

}
