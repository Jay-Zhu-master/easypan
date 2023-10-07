package com.jayzhu.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * 检验参数
     * @return
     */
    boolean checkParams() default false;
    
    /**
     * @description: 校验参数
     * @param : 
     * @return boolean
     * @author: jayzhu
     * @date: 2023/10/7 17:01
     */
    boolean checkLogin() default true;

    /**
     * 校验超级管理员
     * @return
     */
    boolean checkAdmin() default false;
}
