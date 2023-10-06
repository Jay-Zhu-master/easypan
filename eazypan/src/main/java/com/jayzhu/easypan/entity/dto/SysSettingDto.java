package com.jayzhu.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingDto implements Serializable {
    private String registerMailTitle = "邮箱验证码";
    private String registerEmailContent = "您好，您的邮箱验证码是：%s，十五分钟内有效";
    private Integer userInitUserSpace = 5;

    public String getRegisterMailTitle() {

        return registerMailTitle;
    }

    public void setRegisterMailTitle(String registerMailTitle) {
        this.registerMailTitle = registerMailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getUserInitUserSpace() {
        return userInitUserSpace;
    }

    public void setUserInitUserSpace(Integer userInitUserSpace) {
        this.userInitUserSpace = userInitUserSpace;
    }
}
