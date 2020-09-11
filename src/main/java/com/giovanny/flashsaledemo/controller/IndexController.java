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


    public static void main(String[] args) {
        String regex = "^.{2,20}$";
        String name = "2s";
        boolean matches = name.matches(regex);
        System.out.println(matches);

        String idNumRegex = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        String idNum = "513128199508082138";
        boolean matches1 = idNum.matches(idNumRegex);
        System.out.println(matches1);

        String phoneRegex = "^1[2-9]\\d{9}$";
        String phone = "10582457312";
        boolean matches2 = phone.matches(phoneRegex);
        System.out.println(matches2);

    }
}
