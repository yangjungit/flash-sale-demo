package com.giovanny.flashsaledemo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @packageName: com.giovanny.mybatisplusmq.config
 * @className: WebSocketConfig
 * @description: WebSocketConfig
 * @author: YangJun
 * @date: 2020/5/15 10:14
 * @version: v1.0
 **/
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}

