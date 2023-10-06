package com.jayzhu.easypan.mapper;

import com.jayzhu.easypan.entity.po.EmailCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EmailCodeMapper {
    Integer insert(EmailCode emailCode);

    void disableEmailCode(String email);

    EmailCode selectByEmailAndCode(String email, String code);
}
