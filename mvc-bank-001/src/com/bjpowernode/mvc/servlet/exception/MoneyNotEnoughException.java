package com.bjpowernode.mvc.servlet.exception;

/**
 * 处理用户余额不足的异常
 * @version 1.0
 * @since 1.0
 */
public class MoneyNotEnoughException extends Exception{
    public MoneyNotEnoughException() {
    }

    public MoneyNotEnoughException(String message) {
        super(message);
    }
}
