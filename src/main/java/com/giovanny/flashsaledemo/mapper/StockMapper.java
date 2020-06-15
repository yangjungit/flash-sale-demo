package com.giovanny.flashsaledemo.mapper;

import com.giovanny.flashsaledemo.entity.po.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YangJun
 * @since 2020-06-15
 */
public interface StockMapper extends BaseMapper<Stock> {

    List<Stock> findAllIdAndStock();
}
