package com.giovanny.flashsaledemo.service.impl;

import com.giovanny.flashsaledemo.entity.po.Stock;
import com.giovanny.flashsaledemo.mapper.StockMapper;
import com.giovanny.flashsaledemo.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author YangJun
 * @since 2020-06-15
 */
@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean goodsOrder(Long goodsId, Integer count) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        Long stock = (Long) opsForHash.get(StockInit.GOODS_STOCK_KEY, goodsId);
        if (stock == null || stock <= 0 || stock < count) {
            log.info("redis 库存已不充足。stock:{},count:{}", stock, count);
            return false;
        }
        Long increment = opsForHash.increment(StockInit.GOODS_STOCK_KEY, goodsId, -count);
        log.info("increment:{}", increment);
        return true;
    }

    @Override
    public List<Stock> findAllIdAndStock() {

        return stockMapper.findAllIdAndStock();
    }
}
