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
            "    CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END AS peer_id, " +
            "    m.content AS last_content, " +
            "    m.send_time AS last_time, " +
            "    ROW_NUMBER() OVER (PARTITION BY CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END ORDER BY m.send_time DESC) AS rn " +
            "  FROM t_chat_message m " +
            "  LEFT JOIN t_chat_delete_record d ON d.user_id = #{userId} " +
            "    AND d.contact_id = CASE WHEN m.from_user_id = #{userId} THEN m.to_user_id ELSE m.from_user_id END " +
            "    AND d.order_id IS NULL " +
            "  WHERE (m.from_user_id = #{userId} OR m.to_user_id = #{userId}) " +
            "  AND m.send_time > COALESCE(d.delete_time, 0) " +
            ") t WHERE rn = 1")
    List<Map<String, Object>> selectLatestMessageWithEachContact(@Param("userId") Long userId);

    @Select("SELECT COALESCE(MAX(d.delete_time), 0) FROM t_chat_delete_record d " +
            "WHERE d.user_id = #{userId} AND d.contact_id = #{contactId} AND d.order_id IS NULL")
    Long getContactDeleteTime(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT COUNT(*) FROM t_chat_message m " +
            "LEFT JOIN t_chat_delete_record d ON d.user_id = #{userId} AND d.contact_id = #{contactId} AND d.order_id IS NULL " +
            "WHERE m.to_user_id = #{userId} AND m.from_user_id = #{contactId} AND m.read_status = 0 " +
            "AND m.send_time > COALESCE(d.delete_time, 0)")
    Integer countUnreadFromContact(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT " +
            "  order_id AS orderId, " +
            "  last_content AS lastContent, " +
            "  last_time AS lastTime " +
            "FROM ( " +
            "  SELECT " +
            "    m.order_id, " +
            "    m.content AS last_content, " +
            "    m.send_time AS last_time, " +
            "    ROW_NUMBER() OVER (PARTITION BY m.order_id ORDER BY m.send_time DESC) AS rn " +
            "  FROM t_chat_message m " +
            "  LEFT JOIN t_chat_delete_record d ON d.user_id = #{userId} " +
            "    AND d.contact_id = #{contactId} " +
            "    AND d.order_id = m.order_id " +
            "  WHERE ((m.from_user_id = #{userId} AND m.to_user_id = #{contactId}) " +
            "     OR (m.to_user_id = #{userId} AND m.from_user_id = #{contactId})) " +
            "  AND m.send_time > COALESCE(d.delete_time, 0) " +
            ") t WHERE rn = 1")
    List<Map<String, Object>> selectLatestMessageWithContactByOrder(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Select("SELECT COALESCE(MAX(d.delete_time), 0) FROM t_chat_delete_record d " +
            "WHERE d.user_id = #{userId} AND d.contact_id = #{contactId} AND d.order_id = #{orderId}")
    Long getOrderDeleteTime(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("orderId") Long orderId);

    @Select("SELECT COUNT(*) FROM t_chat_message m " +
            "LEFT JOIN t_chat_delete_record d ON d.user_id = #{userId} AND d.contact_id = #{contactId} AND d.order_id = #{orderId} " +
            "WHERE m.to_user_id = #{userId} " +
            "AND m.from_user_id = #{contactId} " +
            "AND m.order_id = #{orderId} " +
            "AND m.read_status = 0 " +
            "AND m.send_time > COALESCE(d.delete_time, 0)")
    Integer countUnreadFromContactInOrder(@Param("userId") Long userId, @Param("contactId") Long contactId, @Param("orderId") Long orderId);
}

