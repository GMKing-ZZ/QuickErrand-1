package com.quickerrand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.common.Result;
import com.quickerrand.service.CreditLevelService;
import com.quickerrand.vo.RunnerCreditVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员信用等级管理控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员信用等级管理接口")
@RestController
@RequestMapping("/admin/creditLevel")
public class AdminCreditLevelController {

    @Autowired
    private CreditLevelService creditLevelService;

    @ApiOperation("获取跑腿员信用等级列表")
    @GetMapping("/list")
    public Result<Page<RunnerCreditVO>> getRunnerCreditList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer creditLevel) {

        Page<RunnerCreditVO> page = creditLevelService.getRunnerCreditList(pageNum, pageSize, keyword, creditLevel);
        return Result.success(page);
    }

    @ApiOperation("手动更新跑腿员信用等级")
    @PutMapping("/{runnerId}")
    public Result<Void> updateCreditLevel(
            @PathVariable Long runnerId,
            @RequestParam Integer creditLevel) {

        if (creditLevel < 1 || creditLevel > 5) {
            return Result.error("信用等级必须在1-5之间");
        }

        creditLevelService.updateCreditLevel(runnerId, creditLevel);
        log.info("手动更新跑腿员信用等级成功，runnerId={}，creditLevel={}", runnerId, creditLevel);
        return Result.success();
    }

    @ApiOperation("重新计算单个跑腿员信用等级")
    @PostMapping("/recalculate/{runnerId}")
    public Result<Integer> recalculateCreditLevel(@PathVariable Long runnerId) {
        Integer creditLevel = creditLevelService.calculateCreditLevel(runnerId);
        creditLevelService.updateCreditLevel(runnerId, creditLevel);
        log.info("重新计算跑腿员信用等级成功，runnerId={}，creditLevel={}", runnerId, creditLevel);
        return Result.success(creditLevel);
    }

    @ApiOperation("批量重新计算所有跑腿员信用等级")
    @PostMapping("/recalculateAll")
    public Result<Void> recalculateAllCreditLevels() {
        creditLevelService.recalculateAllCreditLevels();
        log.info("批量重新计算所有跑腿员信用等级成功");
        return Result.success();
    }
}
