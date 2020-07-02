package com.giovanny.flashsaledemo.controller;


import com.giovanny.flashsaledemo.entity.po.Stock;
import com.giovanny.flashsaledemo.service.IStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YangJun
 * @since 2020-06-15
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private IStockService stockService;

    @PostMapping
    public String addOne(@RequestBody @Validated Stock stock) {
        return null;
    }

    @GetMapping(path = "{id}")
    public String getOne(@PathVariable @NotNull(message = "id不能为空") Long id) {
        for (int i = 0; i < 100; i++) {
            log.info("id:{}", id);
            log.warn("id:{}", id);
            log.debug("id:{}", id);
            log.error("id:{}", id);
        }

        return "success";
    }

    public static void main(String[] args) {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println(hostName + ".." + hostAddress);
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
    }

}
