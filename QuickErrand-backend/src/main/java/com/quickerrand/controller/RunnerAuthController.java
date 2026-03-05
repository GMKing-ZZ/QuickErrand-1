package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.RunnerAuthApplyDTO;
import com.quickerrand.dto.RunnerAuthApprovalDTO;
import com.quickerrand.dto.UpdateRunnerServiceDTO;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.RunnerInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 跑腿员认证控制器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Api(tags = "跑腿员认证接口")
@RestController
@RequestMapping("/runner")
public class RunnerAuthController {

    @Autowired
    private RunnerInfoService runnerInfoService;

    @ApiOperation("申请跑腿员认证")
    @PostMapping("/auth/apply")
    public Result<Void> applyAuth(@Validated @RequestBody RunnerAuthApplyDTO applyDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        runnerInfoService.applyAuth(userId, applyDTO);
        return Result.success();
    }

    @ApiOperation("审核跑腿员认证")
    @PostMapping("/auth/approve")
    public Result<Void> approveAuth(@Validated @RequestBody RunnerAuthApprovalDTO approvalDTO) {
        runnerInfoService.approveAuth(approvalDTO);
        return Result.success();
    }

    @ApiOperation("获取跑腿员认证信息")
    @GetMapping("/auth/info")
    public Result<RunnerInfoVO> getAuthInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        RunnerInfoVO authInfo = runnerInfoService.getAuthInfo(userId);
        return Result.success(authInfo);
    }

    @ApiOperation("更新跑腿员服务设置")
    @PutMapping("/service/settings")
    public Result<String> updateServiceSettings(@Validated @RequestBody UpdateRunnerServiceDTO updateDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        runnerInfoService.updateServiceSettings(userId, updateDTO);
        return Result.success("服务设置更新成功");
    }

}
