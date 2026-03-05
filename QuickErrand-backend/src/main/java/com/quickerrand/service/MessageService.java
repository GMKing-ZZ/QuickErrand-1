package com.quickerrand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quickerrand.vo.MessageVO;

import java.util.List;

/**
 * 消息通知服务接口
 *
 * @author 周政
 * @date 2026-01-27
 */
public interface MessageService {

    /**
     * 获取用户消息列表
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    List<MessageVO> getUserMessages(Long userId);

    /**
     * 获取未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 标记消息为已读
     *
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void markAsRead(Long userId, Long messageId);

    /**
     * 标记所有消息为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 推送消息给用户
     *
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     * @param relatedId 关联ID
     */
    void pushMessage(Long userId, String title, String content, Integer type, Long relatedId);

    /**
     * 推送消息给多个用户
     *
     * @param userIds 用户ID列表
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     * @param relatedId 关联ID
     */
    void pushMessageToUsers(List<Long> userIds, String title, String content, Integer type, Long relatedId);

    /**
     * 删除消息
     *
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void deleteMessage(Long userId, Long messageId);

    /**
     * 分页获取用户消息列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param type 消息类型（可选）
     * @return 分页消息列表
     */
    Page<MessageVO> getUserMessagesPage(Long userId, Integer pageNum, Integer pageSize, Integer type);

    /**
     * 发送系统消息给所有用户
     *
     * @param title 消息标题
     * @param content 消息内容
     * @param relatedId 关联ID（可选）
     * @return 消息ID
     */
    Long sendSystemMessage(String title, String content, Long relatedId);

    /**
     * 发送系统消息给指定用户
     *
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param relatedId 关联ID（可选）
     * @return 消息ID
     */
    Long sendSystemMessageToUser(Long userId, String title, String content, Long relatedId);
}
