package com.quickerrand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quickerrand.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 聊天消息 Mapper
 *
 * 对应表：chat_message
 *
 * @author 周政
 * @date 2026-02-10
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT " +
            "  peer_id AS contactId, " +
            "  last_content AS lastContent, " +
            "  last_time AS lastTime " +
            "FROM ( " +
            "  SELECT " +
            "    CASE WHEN from_user_id = #{userId} THEN to_user_id ELSE from_user_id END AS peer_id, " +
            "    content AS last_content, " +
            "    send_time AS last_time, " +
            "    ROW_NUMBER() OVER (PARTITION BY CASE WHEN from_user_id = #{userId} THEN to_user_id ELSE from_user_id END ORDER BY send_time DESC) AS rn " +
            "  FROM t_chat_message " +
            "  WHERE from_user_id = #{userId} OR to_user_id = #{userId} " +
            ") t WHERE rn = 1")
    List<Map<String, Object>> selectLatestMessageWithEachContact(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM t_chat_message WHERE to_user_id = #{userId} AND from_user_id = #{contactId} AND read_status = 0")
    Integer countUnreadFromContact(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT " +
            "  order_id AS orderId, " +
            "  last_content AS lastContent, " +
            "  last_time AS lastTime " +
            "FROM ( " +
            "  SELECT " +
            "    order_id, " +
            "    content AS last_content, " +
            "    send_time AS last_time, " +
            "    ROW_NUMBER() OVER (PARTITION BY order_id ORDER BY send_time DESC) AS rn " +
            "  FROM t_chat_message " +
            "  WHERE (from_user_id = #{userId} AND to_user_id = #{contactId}) " +
            "     OR (to_user_id = #{userId} AND from_user_id = #{contactId}) " +
            ") t WHERE rn = 1")
    List<Map<String, Object>> selectLatestMessageWithContactByOrder(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT COUNT(*) FROM t_chat_message " +
            "WHERE to_user_id = #{userId} " +
            "AND from_user_id = #{contactId} " +
            "AND order_id = #{orderId} " +
            "AND read_status = 0")
    Integer countUnreadFromContactInOrder(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("orderId") Long orderId);
}

