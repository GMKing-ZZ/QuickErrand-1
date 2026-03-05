package com.quickerrand.websocket;

import com.alibaba.fastjson2.JSON;
import com.quickerrand.utils.JwtUtils;
import com.quickerrand.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息推送 WebSocket 处理器
 *
 * 客户端建立连接时需携带：
 *   /ws/message?token=xxx
 *
 * 推送消息格式（文本 JSON）：
 *   { "id": 1, "title": "xxx", "content": "xxx", "type": 1, "relatedId": 123, ... }
 *
 * @author 周政
 * @date 2026-03-04
 */
@Slf4j
@Component
public class MessagePushHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) {
            log.warn("消息推送 WebSocket 连接缺少查询参数，关闭连接");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Map<String, String> params = parseQuery(uri.getQuery());
        String token = params.get("token");

        if (token == null || token.isEmpty()) {
            log.warn("消息推送 WebSocket 连接参数不完整，token 缺失");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (!jwtUtils.validateToken(token)) {
            log.warn("消息推送 WebSocket 鉴权失败，token 无效或已过期");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("TOKEN_INVALID"));
            return;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);

        session.getAttributes().put("userId", userId);
        USER_SESSIONS.put(userId, session);

        log.info("用户 {} 建立消息推送 WebSocket 连接", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Object userIdObj = session.getAttributes().get("userId");
        if (userIdObj instanceof Long) {
            Long userId = (Long) userIdObj;
            USER_SESSIONS.remove(userId);
            log.info("用户 {} 的消息推送 WebSocket 连接关闭，原因：{}", userId, status);
        } else {
            log.info("未识别会话的消息推送 WebSocket 连接关闭，原因：{}", status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到消息推送 WebSocket 消息: {}", payload);
        
        if ("ping".equalsIgnoreCase(payload) || "heartbeat".equalsIgnoreCase(payload)) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("消息推送 WebSocket 传输错误", exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 推送消息给指定用户
     *
     * @param userId 用户ID
     * @param messageVO 消息内容
     * @return 是否推送成功
     */
    public boolean pushToUser(Long userId, MessageVO messageVO) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = JSON.toJSONString(messageVO);
                session.sendMessage(new TextMessage(message));
                log.info("推送消息给用户 {}，消息ID：{}", userId, messageVO.getId());
                return true;
            } catch (Exception e) {
                log.error("推送消息失败，用户ID: {}", userId, e);
            }
        }
        log.debug("用户 {} 不在线，消息将存储在数据库中", userId);
        return false;
    }

    /**
     * 推送消息给多个用户
     *
     * @param userIds 用户ID列表
     * @param messageVO 消息内容
     */
    public void pushToUsers(java.util.List<Long> userIds, MessageVO messageVO) {
        for (Long userId : userIds) {
            pushToUser(userId, messageVO);
        }
    }

    /**
     * 推送消息给所有在线用户
     *
     * @param messageVO 消息内容
     */
    public void pushToAll(MessageVO messageVO) {
        USER_SESSIONS.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    String message = JSON.toJSONString(messageVO);
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    log.error("推送消息失败，用户ID: {}", userId, e);
                }
            }
        });
        log.info("推送消息给所有在线用户，消息ID：{}，在线用户数：{}", messageVO.getId(), USER_SESSIONS.size());
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineCount() {
        return USER_SESSIONS.size();
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0 && idx < pair.length() - 1) {
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    map.put(key, value);
                } catch (java.io.UnsupportedEncodingException e) {
                    log.error("URL 解析参数失败，query={}", query, e);
                }
            }
        }
        return map;
    }
}
