package com.giovanny.flashsaledemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @packageName: com.giovanny.flashsaledemo.controller
 * @className: IndexController
 * @description: 访问到首页
 * @author: YangJun
 * @date: 2020/6/16 14:53
 * @version: v1.0
 **/
@RestController
public class IndexController {
    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
