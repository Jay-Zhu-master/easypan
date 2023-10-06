package com.jayzhu.easypan.service;

import com.jayzhu.easypan.exception.BusinessException;

public interface EmailCodeService {
    void sendEmailCode(String email,Integer type) throws BusinessException;

    void checkCode(String email,String code);
}
