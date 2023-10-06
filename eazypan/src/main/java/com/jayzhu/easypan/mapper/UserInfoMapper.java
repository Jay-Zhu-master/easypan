package com.jayzhu.easypan.mapper;

import com.jayzhu.easypan.entity.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserInfoMapper {
    UserInfo selectByEmail(String email);

    UserInfo selectByNickName(String nickname);

    void insert(UserInfo userInfo);
    void updateByUserId(UserInfo userInfo, String userId);
}
