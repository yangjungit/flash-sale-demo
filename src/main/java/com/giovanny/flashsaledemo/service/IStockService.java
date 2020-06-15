package com.giovanny.flashsaledemo.service;

import com.giovanny.flashsaledemo.entity.po.Stock;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YangJun
 * @since 2020-06-15
 */
public interface IStockService extends IService<Stock> {

    boolean goodsOrder(Long goodsId, Integer count);

    List<Stock> findAllIdAndStock();
}
