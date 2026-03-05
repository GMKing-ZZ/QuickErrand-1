package com.quickerrand.controller;

import com.quickerrand.common.Result;
import com.quickerrand.service.StatisticsService;
import com.quickerrand.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员统计控制器
 *
 * @author 周政
 * @date 2026-01-27
 */
@Slf4j
@Api(tags = "管理员统计接口")
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取数据看板统计数据")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboardStatistics() {
        DashboardVO vo = statisticsService.getDashboardStatistics();
        return Result.success(vo);
    }
}
