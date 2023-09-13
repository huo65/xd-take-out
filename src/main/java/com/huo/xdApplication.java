package com.huo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Slf4j//lombok提供的日志注解
@SpringBootApplication//表明是SpringBoot的启动类
@EnableOpenApi
@EnableSwagger2
@EnableCaching
@ServletComponentScan
@EnableTransactionManagement
public class xdApplication {
    public static void main(String[] args) {
        SpringApplication.run(xdApplication.class,args);
        log.info("项目启动成功");
    }
}
//http://localhost:8080/backend/page/login/login.html
//http://localhost:8080/front/page/login.html