package com.giovanny.flashsaledemo.common;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;

/**
 * @packageName: com.example.demo1.entity
 * @className: MyResponse
 * @description: MyResponse 统一的返回对象
 * @author: YangJun
 * @date: 2020/4/14 17:02
 * @version: v1.0
 **/
@Data
@Slf4j
public class MyResponse {
    private int code;
    private String message;
    private Object data;

    public MyResponse() {
    }

    public MyResponse(int code) {
        this.code = code;
    }

    public MyResponse(Object data) {
        this.code = 200;
        this.data = data;
    }

    public MyResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public MyResponse(String message, Object data) {
        this.code = 200;
        this.message = message;
        this.data = data;
    }

    public MyResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static void responseJson(ServletResponse response, MyResponse myResponse) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(JSON.toJSONString(myResponse));
        } catch (Exception e) {
            log.error("【JSON输出异常】" + e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }


    public static MyResponse success() {
        return new MyResponse(200);
    }

    public static MyResponse success(String message, Object data) {
        return new MyResponse(message, data);
    }

    public static MyResponse success(Object data) {
        return new MyResponse(data);
    }

    public static MyResponse failed(int code, String message) {
        return new MyResponse(code, message);
    }

    public static MyResponse failed(String message) {
        return new MyResponse(500, message);
    }

    public static MyResponse failed(int code, String message, Object data) {
        return new MyResponse(code, message, data);
    }
}
