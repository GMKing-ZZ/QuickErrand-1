package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单评价Mapper接口（已迁移到 Evaluation）
 *
 * @author 周政
 * @date 2026-01-27
 * @deprecated 请使用 EvaluationMapper
 */
@Deprecated
@Mapper
public interface OrderReviewMapper extends BaseMapper<Evaluation> {
}
