package com.jayzhu.easypan.exception;

import com.jayzhu.easypan.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseVO businessExceptionHandler(Exception e){
        e.printStackTrace();
        return ResponseVO.error(e.getMessage());
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseVO others(Exception e){
//        e.printStackTrace();
//        return ResponseVO.error("对不起操作失败，请联系管理员");
//    }
}
