package com.jayzhu.easypan.service;

import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.po.UserInfo;
import jakarta.servlet.http.HttpSession;

public interface UserInfoService {
    void register(String email, String nickName, String password, String emailCode);

    SessionWebUserDto login(String email,String password);

    void resetPwd(String email, String password, String emailCode);

    SessionWebUserDto getUserInfoFromSession(HttpSession session);
    void updateUserInfoByUserId(UserInfo userInfo, String userId);

    SessionWebUserDto qqlogin(String code);
}
