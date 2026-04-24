package com.quickerrand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.quickerrand.mapper")
@EnableScheduling
public class QuickErrandApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(QuickErrandApplication.class, args);
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        System.out.println("========================================");
        System.out.println("跑腿小程序后端服务启动成功！");
        System.out.println("接口文档地址: http://localhost:8088/api/swagger-ui/");
        System.out.println("========================================");
    }

}
