package com.giovanny.flashsaledemo.entity.po;

import com.giovanny.flashsaledemo.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 * @author YangJun
 * @since 2020-06-15
 */
@Data
public class Stock extends BaseEntity {

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
