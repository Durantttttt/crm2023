package com.bjpowernode.mvc.servlet.exception;

/**
 * 转账失败的异常
 * @version 1.0
 * @since 1.0
 */
public class TransferFalseException extends Exception{
    public TransferFalseException() {
    }

    public TransferFalseException(String message) {
        super(message);
    }
}
