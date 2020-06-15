package com.giovanny.flashsaledemo.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @packageName: com.example.demo1.config.executor
 * @className: ExecutorConfig
 * @description: 线程池配置
 * @author: YangJun
 * @date: 2020/4/10 16:18
 * @version: v1.0
 **/
@Configuration
@ConfigurationProperties(value = "executor")
@Data
@EnableAsync
public class ExecutorConfig {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
    private Integer queueSize;



    @Bean(name = "executor")
    public Executor getExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("taskExecutor-");
        // CallerRunsPolicy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
