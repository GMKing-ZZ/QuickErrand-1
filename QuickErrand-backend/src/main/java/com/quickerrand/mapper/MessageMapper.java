package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息通知Mapper
 *
 * @author 周政
 * @date 2026-01-27
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
