package com.campus.market.exception;


//业务异常 - 用户操作不当导致的错误（如余额不足、学号重复）,这类异常需要向用户显示具体原因

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}