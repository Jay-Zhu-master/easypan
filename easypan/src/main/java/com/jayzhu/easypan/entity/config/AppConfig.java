package com.jayzhu.easypan.entity.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {
    @Value("${spring.mail.username}")
    private String sendUsername;
    @Value("${admin.emails}")
    private String adminEmails;
    @Value("${project.folder}")
    private String projectFolder;
    @Value("${qq.url.authorization}")
    private String qqUrlAuthorization;
    @Value("${qq.app.id}")
    private String qqAppId;
    @Value("${qq.app.key}")
    private String qqAppKey;
    @Value("${qq.url.access.token}")
    private String qqUrlAccessToken;
    @Value("${qq.url.openid}")
    private String qqUrlOpenId;
    @Value("${qq.url.user.info}")
    private String qqUrlUserInfo;
    @Value("${qq.url.redirect}")
    private String qqUrlRedirect;
}
