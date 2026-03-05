package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickerrand.entity.Evaluation;
import com.quickerrand.vo.OrderReviewListVO;

/**
 * 订单评价Service接口（已迁移到 Evaluation）
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface OrderReviewService extends IService<Evaluation> {

    /**
     * 获取评价列表（分页）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param rating 评分（平均分）
     * @param status 状态
     * @return 评价分页列表
     */
    Page<OrderReviewListVO> getReviewList(Integer pageNum, Integer pageSize, String keyword, Integer rating, Integer status);

    /**
     * 获取用户的评价列表（分页）
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户评价分页列表
     */
    Page<OrderReviewListVO> getUserReviewList(Long userId, Integer pageNum, Integer pageSize);
}
