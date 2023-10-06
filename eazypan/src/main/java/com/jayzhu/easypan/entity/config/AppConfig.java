package com.jayzhu.easypan.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${spring.mail.username}")
    private String sendUsername;

    @Value("${admin.emails}")
    private String adminEmails;


    public String getAdminEmails() {
        return adminEmails;
    }

    public String getSendUsername() {
        return sendUsername;
    }

}
