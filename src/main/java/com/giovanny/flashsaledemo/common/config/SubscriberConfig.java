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
 *               对于RabbitMQ和Redis的入队和出队操作，各执行100万次，每10万次记录一次执行时间。
 *               测试数据分为128Bytes、512Bytes、1K和10K四个不同大小的数据。
 *               实验表明：入队时，当数据比较小时Redis的性能要高于RabbitMQ，而如果数据大小超过了10K，Redis则慢的无法忍受；
 *               出队时，无论数据大小，Redis都表现出非常好的性能，而RabbitMQ的出队性能则远低于Redis。
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
