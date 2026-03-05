package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.OrderType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单类型Mapper接口
 *
 * @author 周政
 * @date 2026-01-26
 */
@Mapper
public interface OrderTypeMapper extends BaseMapper<OrderType> {
}
