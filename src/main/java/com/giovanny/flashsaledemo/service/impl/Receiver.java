package com.giovanny.flashsaledemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @packageName: com.giovanny.flashsaledemo.common.config
 * @className: Receiver
 * @description: Receiver
 * @author: YangJun
 * @date: 2020/6/16 14:19
 * @version: v1.0
 **/
@Service
@Slf4j
public class Receiver implements MessageListener {
    @Autowired
    private StockServiceImpl stockService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // redis队列的方式不是很好 很容易丢失消息，也可能是我没有配置好，感觉用rabbitmq会好一点
        log.info("message:{}", message);
        log.info("pattern:{}", pattern);
        //减库存，生成订单
        String msg = message.toString();
        JSONObject object = JSONObject.parseObject(msg);
        Integer goodsId = (Integer) object.get("id");
        Integer count = (Integer) object.get("count");
        stockService.countDown(Long.valueOf(goodsId), count);
    }
}
