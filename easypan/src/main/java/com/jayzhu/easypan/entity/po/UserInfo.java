package com.jayzhu.easypan.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    String userId;
    String nickName;
    String email;
    String qqOpenId;
    String qqAvatar;
    String password;
    LocalDateTime joinTime;
    LocalDateTime lastLoginTime;
    Integer status;
    Long useSpace;
    Long totalSpace;
}
