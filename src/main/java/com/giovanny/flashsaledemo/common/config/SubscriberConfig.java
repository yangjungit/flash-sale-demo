package com.giovanny.flashsaledemo.common.config;

import com.giovanny.flashsaledemo.service.impl.Receiver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


/**
 * @packageName: com.giovanny.flashsaledemo.common.config
 * @className: SubscriberConfig
 * @description: redis消息订阅配置
 * @author: YangJun
 * @date: 2020/6/16 13:56
 * @version: v1.0
 **/
@Configuration
@AutoConfigureAfter({Receiver.class})
public class SubscriberConfig {

    @Bean
    public MessageListenerAdapter getMessageListenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver);
    }

    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory factory, MessageListenerAdapter adapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(factory);
        redisMessageListenerContainer.addMessageListener(adapter, new PatternTopic("redis-topic"));
        return redisMessageListenerContainer;
    }
}
