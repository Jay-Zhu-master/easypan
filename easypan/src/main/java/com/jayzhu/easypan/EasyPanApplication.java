package com.jayzhu.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.jayzhu.easypan")
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
@MapperScan(basePackages = {"com.jayzhu.easypan.mapper"})
public class EasyPanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyPanApplication.class,args);
    }
}
