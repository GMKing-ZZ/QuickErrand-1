package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.ChatOrderRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单-聊天绑定 Mapper
 *
 * 对应表：chat_order_rel
 *
 * @author 周政
 * @date 2026-02-10
 */
@Mapper
public interface ChatOrderRelMapper extends BaseMapper<ChatOrderRel> {
}

