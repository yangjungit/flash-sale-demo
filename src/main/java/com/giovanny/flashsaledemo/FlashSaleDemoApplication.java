package com.giovanny.flashsaledemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@Slf4j
public class FlashSaleDemoApplication {

    public static void main(String[] args) {
        try {
            System.setProperty("LOG_IP", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        SpringApplication.run(FlashSaleDemoApplication.class, args);
    }

}
