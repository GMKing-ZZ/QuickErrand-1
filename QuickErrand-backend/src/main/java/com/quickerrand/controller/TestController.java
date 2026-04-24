package com.quickerrand.controller;

import com.quickerrand.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ApiOperation("测试接口")
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, QuickErrand!");
    }

    @ApiOperation("生成BCrypt密码")
    @GetMapping("/encode")
    public Result<String> encodePassword(@RequestParam("password") String password) {
        String encoded = passwordEncoder.encode(password);
        return Result.success(encoded);
    }

}
