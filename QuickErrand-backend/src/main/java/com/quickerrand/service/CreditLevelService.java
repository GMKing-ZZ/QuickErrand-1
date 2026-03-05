package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.vo.RunnerCreditVO;

/**
 * 信用等级服务接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface CreditLevelService {

    /**
     * 获取跑腿员信用等级列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词（姓名/手机号）
     * @param creditLevel 信用等级
     * @return 分页结果
     */
    Page<RunnerCreditVO> getRunnerCreditList(Integer pageNum, Integer pageSize, String keyword, Integer creditLevel);

    /**
     * 计算跑腿员信用等级
     *
     * @param runnerId 跑腿员ID
     * @return 信用等级（1-5星）
     */
    Integer calculateCreditLevel(Long runnerId);

    /**
     * 更新跑腿员信用等级
     *
     * @param runnerId 跑腿员ID
     * @param creditLevel 信用等级
     */
    void updateCreditLevel(Long runnerId, Integer creditLevel);

    /**
     * 批量重新计算所有跑腿员信用等级
     */
    void recalculateAllCreditLevels();
}
