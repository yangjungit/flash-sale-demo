package com.giovanny.flashsaledemo.common.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: //
 * @author: YangJun
 * @date: 2021/02/04 19:40
 * @version: v1.0
 **/
public class BackConfigAdapter  implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ReqMdcLogInterceptor());
    }
}
