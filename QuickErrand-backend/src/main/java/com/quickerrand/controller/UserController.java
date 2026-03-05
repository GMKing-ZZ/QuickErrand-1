package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.dto.ChangePasswordDTO;
import com.quickerrand.dto.UpdateUserInfoDTO;
import com.quickerrand.service.OrderReviewService;
import com.quickerrand.service.UserService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.OrderReviewListVO;
import com.quickerrand.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderReviewService orderReviewService;

    @ApiOperation("获取个人信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/info")
    public Result<String> updateUserInfo(@Validated @RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateUserInfo(userId, updateUserInfoDTO);
        return Result.success("更新成功");
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<String> changePassword(@Validated @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, changePasswordDTO);
        return Result.success("密码修改成功");
    }

    @ApiOperation("获取我的评价列表")
    @GetMapping("/reviews")
    public Result<Page<OrderReviewListVO>> getMyReviews(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<OrderReviewListVO> page = orderReviewService.getUserReviewList(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("删除当前用户（用于微信登录后未设置密码的情况）")
    @DeleteMapping("/current")
    public Result<String> deleteCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.deleteCurrentUser(userId);
        return Result.success("用户删除成功");
    }
}
