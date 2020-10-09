package com.giovanny.flashsaledemo.exception;

/**
 * @packageName: com.giovanny.flashsaledemo.exception
 * @className: ResultCode
 * @description: //TODO
 * @author: YangJun
 * @date: 2020/7/3 16:04
 * @version: v1.0
 **/
public enum ResultCode implements IResultCode{
    /**
     * 自定义code及信息
     */
    SYSTEM_LOGIC_ERROR(9001, "系统错误"),
    RPC_ERROR(9002, "远程服务调用失败"),
    AUTHENTICATION_ERROR(9003, "验证失败 请重新登录"),
    SESSION_INVALID(9004, "session失效 请重新登录"),
    ;

    private int code;
    private String desc;

    ResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.desc;
    }
}
