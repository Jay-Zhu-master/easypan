package com.jayzhu.easypan.exception;

import com.jayzhu.easypan.enums.ResponseCodeEnum;

public class BusinessException extends RuntimeException {
    private Integer code;
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException() {
        super();
    }

    public BusinessException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMsg());
        code = responseCodeEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
