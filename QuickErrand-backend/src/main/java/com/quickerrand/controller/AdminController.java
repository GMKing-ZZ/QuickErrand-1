package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.AdminLoginDTO;
import com.quickerrand.dto.ChangePasswordDTO;
import com.quickerrand.dto.UpdateUserInfoDTO;
import com.quickerrand.entity.User;
import com.quickerrand.service.UserService;
import com.quickerrand.utils.JwtUtils;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.AdminLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Validated @RequestBody AdminLoginDTO loginDTO) {
        log.info("管理员登录请求，用户名：{}", loginDTO.getUsername());
        
        // 查询用户
        User user = userService.getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            log.warn("用户不存在：{}", loginDTO.getUsername());
            return Result.error("用户名或密码错误");
        }

        log.info("查询到用户：id={}, username={}, userType={}", user.getId(), user.getUsername(), user.getUserType());

        // 验证用户类型（必须是管理员）
        if (user.getUserType() != 3) {
            log.warn("用户类型不是管理员：{}", user.getUserType());
            return Result.error("无权限访问管理后台");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("密码验证失败");
            return Result.error("用户名或密码错误");
        }

        // 生成token（使用 username 或 phone）
        String tokenSubject = user.getUsername() != null ? user.getUsername() : user.getPhone();
        String token = jwtUtils.generateToken(user.getId(), tokenSubject);

        // 构造响应
        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);

        AdminLoginVO.AdminUserInfo userInfo = new AdminLoginVO.AdminUserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername() != null ? user.getUsername() : user.getPhone()); // 优先使用username
        userInfo.setNickname(user.getNickname());
        userInfo.setUserType(user.getUserType());
        userInfo.setAvatar(user.getAvatar());
        vo.setUserInfo(userInfo);

        log.info("管理员{}登录成功", user.getUsername() != null ? user.getUsername() : user.getPhone());
        return Result.success(vo);
    }

    @ApiOperation("获取管理员信息")
    @GetMapping("/info")
    public Result<AdminLoginVO.AdminUserInfo> getAdminInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        if (user == null || user.getUserType() != 3) {
            return Result.error("无权限访问");
        }

        AdminLoginVO.AdminUserInfo userInfo = new AdminLoginVO.AdminUserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername() != null ? user.getUsername() : user.getPhone()); // 优先使用username
        userInfo.setNickname(user.getNickname());
        userInfo.setUserType(user.getUserType());
        userInfo.setAvatar(user.getAvatar());

        return Result.success(userInfo);
    }

    @ApiOperation("更新管理员信息")
    @PutMapping("/info")
    public Result<Void> updateAdminInfo(@Validated @RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        if (user == null || user.getUserType() != 3) {
            return Result.error("无权限访问");
        }

        userService.updateUserInfo(userId, updateUserInfoDTO);
        log.info("管理员{}更新信息成功", userId);
        return Result.success();
    }

    @ApiOperation("修改管理员密码")
    @PutMapping("/password")
    public Result<Void> updateAdminPassword(@Validated @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userService.getUserById(userId);

        if (user == null || user.getUserType() != 3) {
            return Result.error("无权限访问");
        }

        // 验证原密码
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            return Result.error("原密码错误");
        }

        userService.changePassword(userId, changePasswordDTO);
        log.info("管理员{}修改密码成功", userId);
        return Result.success();
    }

    @ApiOperation("管理员退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("管理员{}退出登录", userId);
        return Result.success();
    }
}
