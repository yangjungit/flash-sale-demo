package com.giovanny.flashsaledemo.exception;

import lombok.Getter;

@Getter
public class BaseRuntimeException extends RuntimeException {

    private IResultCode resultCode;

    public BaseRuntimeException(IResultCode resultCode) {
        super();
        this.resultCode = resultCode;
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(Throwable cause, IResultCode resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }
}
