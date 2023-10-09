package com.jayzhu.easypan.mapper;

import com.jayzhu.easypan.entity.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserInfoMapper {
    UserInfo selectByEmail(String email);

    UserInfo selectByNickName(String nickname);

    void insert(UserInfo userInfo);
    void updateByUserId(UserInfo userInfo, String userId);

    Integer updateUseSpace(@Param("userId") String userId,@Param("useSpace") Long useSpace,@Param("totalSpace") Long totalSpace);
}
