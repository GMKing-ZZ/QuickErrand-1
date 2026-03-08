package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.service.RunnerBlacklistService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.BlacklistedRunnerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "跑腿员黑名单接口")
@RestController
@RequestMapping("/blacklist")
public class RunnerBlacklistController {

    @Autowired
    private RunnerBlacklistService runnerBlacklistService;

    @ApiOperation("拉黑跑腿员")
    @PostMapping("/add")
    public Result<Void> addToBlacklist(@RequestBody AddToBlacklistRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        runnerBlacklistService.addToBlacklist(userId, request.getRunnerId(), request.getReason());
        return Result.success();
    }

    @ApiOperation("取消拉黑跑腿员")
    @DeleteMapping("/remove/{runnerId}")
    public Result<Void> removeFromBlacklist(@PathVariable Long runnerId) {
        Long userId = SecurityUtils.getCurrentUserId();
        runnerBlacklistService.removeFromBlacklist(userId, runnerId);
        return Result.success();
    }

    @ApiOperation("获取黑名单列表")
    @GetMapping("/list")
    public Result<List<BlacklistedRunnerVO>> getBlacklist() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<BlacklistedRunnerVO> list = runnerBlacklistService.getBlacklist(userId);
        return Result.success(list);
    }

    @ApiOperation("检查是否已拉黑")
    @GetMapping("/check/{runnerId}")
    public Result<Boolean> checkBlacklisted(@PathVariable Long runnerId) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isBlacklisted = runnerBlacklistService.isBlacklisted(userId, runnerId);
        return Result.success(isBlacklisted);
    }

    @lombok.Data
    public static class AddToBlacklistRequest {
        private Long runnerId;
        private String reason;
    }
}
