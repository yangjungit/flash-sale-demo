package com.giovanny.flashsaledemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.giovanny.flashsaledemo.mapper"})
public class FlashSaleDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashSaleDemoApplication.class, args);
    }

}
