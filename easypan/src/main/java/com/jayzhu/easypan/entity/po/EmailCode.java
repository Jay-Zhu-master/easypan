package com.jayzhu.easypan.entity.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmailCode {
    private String code;
    private String email;
    private Integer status;
    private LocalDateTime createTime;

    public EmailCode() {
    }

    public EmailCode(String code, String email, Integer status, LocalDateTime createTime) {
        this.code = code;
        this.email = email;
        this.status = status;
        this.createTime = createTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
