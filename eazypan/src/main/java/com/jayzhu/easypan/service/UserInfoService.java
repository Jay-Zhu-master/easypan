package com.jayzhu.easypan.service;

import com.jayzhu.easypan.entity.dto.SessionWebUserDto;

public interface UserInfoService {
    void register(String email, String nickName, String password, String emailCode);

    SessionWebUserDto login(String email,String password);
}
