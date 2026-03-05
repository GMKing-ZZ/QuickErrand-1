package com.quickerrand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 跑腿小程序后端服务启动类
 *
 * @author 周政
 * @date 2026-01-26
 */
@SpringBootApplication
@MapperScan("com.quickerrand.mapper")
@EnableScheduling
public class QuickErrandApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickErrandApplication.class, args);
        System.out.println("========================================");
        System.out.println("跑腿小程序后端服务启动成功！");
        System.out.println("接口文档地址: http://localhost:8088/api/swagger-ui/");
        System.out.println("========================================");
    }

}
