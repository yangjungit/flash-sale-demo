package com.giovanny.flashsaledemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.giovanny.flashsaledemo.exception.BaseRuntimeException;
import com.giovanny.flashsaledemo.exception.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
        // redis队列
        log.info("message:{}", message);
        log.info("pattern:{}", pattern);
        // channel
        String channel = new String(pattern);
        //减库存，生成订单 这里只减了库存
        String msg = message.toString();
        JSONObject object = JSONObject.parseObject(msg);
        Integer goodsId = (Integer) object.get("id");
        Integer count = (Integer) object.get("count");
        String userId = (String) object.get("userId");
        stockService.countDown(Long.valueOf(goodsId), count);
        try {
            // 消息内容可以是其它内容，比如返回订单信息，前端直接跳转到订单页面，去支付
            WebSocketServer.sendInfo("抢购成功", userId);
        } catch (IOException e) {
            log.error("send webSocket msg err:[{}]", e.getMessage());
            throw new BaseRuntimeException(ResultCode.SYSTEM_LOGIC_ERROR);
        }
    }
}
