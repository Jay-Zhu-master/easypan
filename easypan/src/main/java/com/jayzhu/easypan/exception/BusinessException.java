package com.jayzhu.easypan.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException() {
        super();
    }
}
