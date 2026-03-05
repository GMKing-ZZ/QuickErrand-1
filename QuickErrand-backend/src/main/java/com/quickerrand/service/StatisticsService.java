package com.quickerrand.service;

import com.quickerrand.vo.DashboardVO;

/**
 * 统计Service接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface StatisticsService {

    /**
     * 获取数据看板统计数据
     *
     * @return 数据看板VO
     */
    DashboardVO getDashboardStatistics();
}
