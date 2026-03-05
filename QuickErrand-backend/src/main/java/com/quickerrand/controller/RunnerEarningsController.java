package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.dto.WithdrawalApplyDTO;
import com.quickerrand.service.EarningsService;
import com.quickerrand.service.WithdrawalService;
import com.quickerrand.utils.SecurityUtils;
import com.quickerrand.vo.EarningsDetailVO;
import com.quickerrand.vo.EarningsStatisticsVO;
import com.quickerrand.vo.WithdrawalRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跑腿员收益管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "跑腿员收益管理接口")
@RestController
@RequestMapping("/runner/earnings")
public class RunnerEarningsController {

    @Autowired
    private EarningsService earningsService;

    @Autowired
    private WithdrawalService withdrawalService;

    @ApiOperation("获取收益统计")
    @GetMapping("/statistics")
    public Result<EarningsStatisticsVO> getEarningsStatistics() {
        Long userId = SecurityUtils.getCurrentUserId();
        EarningsStatisticsVO statistics = earningsService.getEarningsStatistics(userId);
        return Result.success(statistics);
    }

    @ApiOperation("获取收益明细列表")
    @GetMapping("/details")
    public Result<List<EarningsDetailVO>> getEarningsDetails() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<EarningsDetailVO> details = earningsService.getEarningsDetails(userId);
        return Result.success(details);
    }

    @ApiOperation("申请提现")
    @PostMapping("/withdrawal/apply")
    public Result<Void> applyWithdrawal(@Validated @RequestBody WithdrawalApplyDTO applyDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        withdrawalService.applyWithdrawal(userId, applyDTO);
        return Result.success();
    }

    @ApiOperation("获取提现记录列表")
    @GetMapping("/withdrawal/records")
    public Result<List<WithdrawalRecordVO>> getWithdrawalRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<WithdrawalRecordVO> records = withdrawalService.getWithdrawalRecords(userId);
        return Result.success(records);
    }
}
