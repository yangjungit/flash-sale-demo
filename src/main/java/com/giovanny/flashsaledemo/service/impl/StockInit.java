package com.giovanny.flashsaledemo.service.impl;

import com.giovanny.flashsaledemo.entity.po.Stock;
import com.giovanny.flashsaledemo.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @packageName: com.giovanny.flashsaledemo.service.impl
 * @className: StockInit
 * @description: 商品库存初始化
 * @author: YangJun
 * @date: 2020/6/15 11:50
 * @version: v1.0
 **/
@Component
@Slf4j
public class StockInit implements InitializingBean {
    @Autowired
    private IStockService stockService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public final static String GOODS_STOCK_KEY = "GOODS_STOCK_KEY";

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("----初始化商品库存，将MySQL中的商品库存保存到Redis----");
        log.warn("----初始化商品库存，将MySQL中的商品库存保存到Redis----");
        log.error("----初始化商品库存，将MySQL中的商品库存保存到Redis----");
        Map<Long, Long> stockMap = stockService.list()
                .parallelStream()
                .collect(Collectors.toMap(Stock::getId, Stock::getStock));
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.putAll(GOODS_STOCK_KEY, stockMap);
        redisTemplate.expire(GOODS_STOCK_KEY, 5, TimeUnit.MINUTES);
    }
}
