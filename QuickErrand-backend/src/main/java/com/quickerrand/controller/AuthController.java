package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.LoginDTO;
import com.quickerrand.dto.RegisterDTO;
import com.quickerrand.dto.ResetPasswordDTO;
import com.quickerrand.dto.WxLoginDTO;
import com.quickerrand.service.SmsService;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

/**
 * 认证控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "认证接口")
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @ApiOperation("发送验证码")
    @PostMapping("/sendCode")
    public Result<String> sendCode(@RequestParam @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        smsService.sendCode(phone);
        return Result.success("验证码发送成功");
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功");
    }

    @ApiOperation("密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @ApiOperation("微信登录")
    @PostMapping("/wxLogin")
    public Result<LoginVO> wxLogin(@Validated @RequestBody WxLoginDTO wxLoginDTO) {
        LoginVO loginVO = userService.wxLogin(wxLoginDTO);
        return Result.success(loginVO);
    }

    @ApiOperation("重置密码（忘记密码）")
    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return Result.success("密码重置成功");
    }

    @ApiOperation("测试接口（需要登录）")
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("认证成功");
    }

}
