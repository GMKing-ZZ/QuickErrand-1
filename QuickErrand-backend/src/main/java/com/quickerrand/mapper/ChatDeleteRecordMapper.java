package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.ChatDeleteRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天删除记录 Mapper
 *
 * @author 周政
 * @date 2026-03-08
 */
@Mapper
public interface ChatDeleteRecordMapper extends BaseMapper<ChatDeleteRecord> {

    @Select("SELECT delete_time FROM t_chat_delete_record " +
            "WHERE user_id = #{userId} AND contact_id = #{contactId} AND order_id IS NULL " +
            "ORDER BY delete_time DESC LIMIT 1")
    Long getLatestDeleteTimeByContact(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT delete_time FROM t_chat_delete_record " +
            "WHERE user_id = #{userId} AND contact_id = #{contactId} AND order_id = #{orderId} " +
            "ORDER BY delete_time DESC LIMIT 1")
    Long getLatestDeleteTimeByOrder(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("orderId") Long orderId);

    @Select("SELECT order_id FROM t_chat_delete_record " +
            "WHERE user_id = #{userId} AND contact_id = #{contactId} AND order_id IS NOT NULL")
    List<Long> getDeletedOrderIds(@Param("userId") Long userId, @Param("contactId") Long contactId);
}
