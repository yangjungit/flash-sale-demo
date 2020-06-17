package com.giovanny.flashsaledemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.giovanny.flashsaledemo.entity.po.Stock;
import com.giovanny.flashsaledemo.mapper.StockMapper;
import com.giovanny.flashsaledemo.service.IStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Integer stock = (Integer) opsForHash.get(StockInit.GOODS_STOCK_KEY, goodsId);
        if (stock == null) {
            log.info("redis 此商品[{}]秒杀已结束。stock:{}", goodsId, stock);
            return false;
        }
        if (stock <= 0 || stock < count) {
            log.info("redis 库存已不充足。stock:{},count:{}", stock, count);
            return false;
        }
        Long lastStock = opsForHash.increment(StockInit.GOODS_STOCK_KEY, goodsId, -count);
        log.info("剩余库存 lastStock:{}", lastStock);
        Map<String, Object> map = new HashMap<>(8);
        map.put("id", goodsId);
        map.put("count", count);
        redisTemplate.convertAndSend("redis-topic", map);
        return true;
    }

    @Override
    public List<Stock> findAllIdAndStock() {

        return stockMapper.findAllIdAndStock();
    }

    @Override
    public void countDown(Long goodsId, Integer count) {
        stockMapper.countDownStock(goodsId, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoods() throws Exception {
        Stock stock = new Stock();
        stock.setStock(500L);
        stock.setName("vivo");
        stock.setVersion("3");
        QueryWrapper<Stock> wrapper = new QueryWrapper<>();
        List<Stock> stocks1 = stockMapper.selectList(wrapper);
        System.out.println(stocks1.size());
        stockMapper.insert(stock);
        List<Stock> stocks2 = stockMapper.selectList(wrapper);
        System.out.println(stocks2.size());
    }
}
