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

    VERIFY_SIGN_HEAD_MISS(9101, "sign api head param miss"),
    VERIFY_SIGN_PUBLIC_KEY_MISS(9102, "sign api public key miss"),
    VERIFY_SIGN_FAIL(9103, "sign api check error"),

    XSTORE_UPLOAD_FAIL(9201, "xstore upload error"),
    XSTORE_DOWNLOAD_FAIL(9202, "xstore download error"),
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
