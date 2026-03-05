package com.quickerrand.service;

import com.quickerrand.vo.EarningsDetailVO;
import com.quickerrand.vo.EarningsStatisticsVO;

import java.util.List;

/**
 * 收益服务接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface EarningsService {

    /**
     * 获取收益统计
     *
     * @param userId 用户ID
     * @return 收益统计信息
     */
    EarningsStatisticsVO getEarningsStatistics(Long userId);

    /**
     * 获取收益明细列表
     *
     * @param userId 用户ID
     * @return 收益明细列表
     */
    List<EarningsDetailVO> getEarningsDetails(Long userId);
}
