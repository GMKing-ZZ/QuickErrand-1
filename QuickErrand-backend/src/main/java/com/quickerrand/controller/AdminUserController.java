package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.dto.CreateUserDTO;
import com.quickerrand.dto.UserQueryDTO;
import com.quickerrand.service.UserService;
import com.quickerrand.vo.UserDetailVO;
import com.quickerrand.vo.UserListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员用户管理接口")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public Result<Page<UserListVO>> getUserList(UserQueryDTO queryDTO) {
        Page<UserListVO> page = userService.getUserList(queryDTO);
        return Result.success(page);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/detail/{userId}")
    public Result<UserDetailVO> getUserDetail(@PathVariable Long userId) {
        UserDetailVO detail = userService.getUserDetail(userId);
        return Result.success(detail);
    }

    @ApiOperation("更新用户状态")
    @PostMapping("/status/{userId}")
    public Result<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        if (status != 0 && status != 1) {
            return Result.error("状态参数错误");
        }
        userService.updateUserStatus(userId, status);
        return Result.success();
    }

    @ApiOperation("创建用户")
    @PostMapping
    public Result<Void> createUser(@org.springframework.web.bind.annotation.RequestBody @javax.validation.Valid CreateUserDTO createUserDTO) {
        userService.createUser(createUserDTO);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return Result.success();
    }

    @ApiOperation("批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> ids = requestBody.get("ids");
        userService.batchDeleteUsers(ids);
        return Result.success();
    }
}
