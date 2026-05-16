package com.campus.market.exception;

//系统异常 - 系统内部错误（如数据库连接失败、SQL错误）,这类异常不需要向用户显示具体原因，只显示"系统繁忙"

public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}