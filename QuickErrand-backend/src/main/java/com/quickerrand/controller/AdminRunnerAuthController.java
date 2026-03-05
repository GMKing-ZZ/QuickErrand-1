package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.dto.RunnerAuthApprovalDTO;
import com.quickerrand.service.RunnerInfoService;
import com.quickerrand.vo.RunnerAuthListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员跑腿员认证管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员跑腿员认证管理接口")
@RestController
@RequestMapping("/admin/runnerAuth")
public class AdminRunnerAuthController {

    @Autowired
    private RunnerInfoService runnerInfoService;

    @ApiOperation("获取认证申请列表")
    @GetMapping("/list")
    public Result<Page<RunnerAuthListVO>> getAuthApplicationList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer certStatus,
            @RequestParam(required = false) String keyword) {

        Page<RunnerAuthListVO> page = runnerInfoService.getAuthApplicationList(pageNum, pageSize, certStatus, keyword);
        return Result.success(page);
    }

    @ApiOperation("审核认证申请")
    @PostMapping("/approve")
    public Result<Void> approveAuth(@Validated @RequestBody RunnerAuthApprovalDTO approvalDTO) {
        runnerInfoService.approveAuth(approvalDTO);
        return Result.success();
    }

    @ApiOperation("删除认证申请记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteAuthApplication(@PathVariable Long id) {
        runnerInfoService.deleteAuthApplication(id);
        return Result.success();
    }

    @ApiOperation("批量删除认证申请记录")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteAuthApplications(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        runnerInfoService.batchDeleteAuthApplications(ids);
        return Result.success();
    }
}
