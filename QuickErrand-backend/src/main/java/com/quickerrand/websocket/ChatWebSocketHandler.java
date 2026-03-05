package com.quickerrand.websocket;

import com.alibaba.fastjson2.JSON;
import com.quickerrand.service.ChatService;
import com.quickerrand.utils.JwtUtils;
import com.quickerrand.vo.ChatMessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 * 聊天 WebSocket 处理器
 *
 * 客户端建立连接时需携带：
 *   /ws/chat?token=xxx&orderId=123
 *
 * 消息格式（文本 JSON）：
 *   { "content": "你好" }
 *
 * @author 周政
 * @date 2026-02-10
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /**
     * key: orderId:userId
     * value: WebSocketSession
     */
    private static final Map<String, WebSocketSession> ORDER_USER_SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ChatService chatService;

    private String buildKey(Long orderId, Long userId) {
        return orderId + ":" + userId;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) {
            log.warn("WebSocket 连接缺少查询参数，关闭连接");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Map<String, String> params = parseQuery(uri.getQuery());
        String token = params.get("token");
        String orderIdStr = params.get("orderId");

        if (token == null || token.isEmpty() || orderIdStr == null || orderIdStr.isEmpty()) {
            log.warn("WebSocket 连接参数不完整，token 或 orderId 缺失");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (!jwtUtils.validateToken(token)) {
            log.warn("WebSocket 鉴权失败，token 无效或已过期");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("TOKEN_INVALID"));
            return;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        Long orderId;
        try {
            orderId = Long.parseLong(orderIdStr);
        } catch (NumberFormatException e) {
            log.warn("WebSocket 连接参数 orderId 非法: {}", orderIdStr);
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        try {
            // 校验订单与聊天会话合法性，如无记录则自动创建
            chatService.ensureChatSession(userId, orderId);

            // 记录到本地映射
            session.getAttributes().put("userId", userId);
            session.getAttributes().put("orderId", orderId);
            ORDER_USER_SESSIONS.put(buildKey(orderId, userId), session);

            log.info("用户 {} 在订单 {} 上建立聊天 WebSocket 连接", userId, orderId);
        } catch (Exception e) {
            log.warn("WebSocket 连接失败，原因：{}", e.getMessage());
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason(e.getMessage()));
            return;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Object userIdObj = session.getAttributes().get("userId");
        Object orderIdObj = session.getAttributes().get("orderId");
        if (userIdObj instanceof Long && orderIdObj instanceof Long) {
            Long userId = (Long) userIdObj;
            Long orderId = (Long) orderIdObj;
            ORDER_USER_SESSIONS.remove(buildKey(orderId, userId));
            log.info("用户 {} 在订单 {} 的聊天 WebSocket 连接关闭，原因：{}", userId, orderId, status);
        } else {
            log.info("未识别会话的聊天 WebSocket 连接关闭，原因：{}", status);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Object userIdObj = session.getAttributes().get("userId");
        Object orderIdObj = session.getAttributes().get("orderId");
        if (!(userIdObj instanceof Long) || !(orderIdObj instanceof Long)) {
            log.warn("收到消息但缺少会话上下文，主动关闭连接");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Long fromUserId = (Long) userIdObj;
        Long orderId = (Long) orderIdObj;
        String payload = message.getPayload();
        log.info("收到聊天消息原始报文，orderId={}, fromUserId={}, payload={}", orderId, fromUserId, payload);

        if (payload == null || payload.trim().isEmpty()) {
            return;
        }

        Map<String, Object> json;
        try {
            json = JSON.parseObject(payload);
        } catch (Exception e) {
            log.warn("聊天消息解析失败，payload={}", payload, e);
            return;
        }

        Object contentObj = json.get("content");
        if (contentObj == null) {
            return;
        }
        String content = String.valueOf(contentObj).trim();
        if (content.isEmpty()) {
            return;
        }

        // 消息类型，默认为文字消息
        Integer msgType = 1;
        Object msgTypeObj = json.get("msgType");
        if (msgTypeObj != null) {
            try {
                msgType = Integer.parseInt(String.valueOf(msgTypeObj));
            } catch (NumberFormatException e) {
                log.warn("消息类型解析失败，使用默认值 1（文字）");
            }
        }

        // 保存消息
        ChatMessageVO saved = chatService.saveMessage(fromUserId, orderId, content, msgType);

        // 发送给发送方（self = true）
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(saved)));
        } catch (Exception e) {
            log.error("向发送方推送消息失败，orderId={}, fromUserId={}", orderId, fromUserId, e);
        }

        // 发送给接收方（如果在线，self = false）
        Long toUserId = saved.getToUserId();
        WebSocketSession peerSession = ORDER_USER_SESSIONS.get(buildKey(orderId, toUserId));
        if (peerSession != null && peerSession.isOpen()) {
            ChatMessageVO toSend = new ChatMessageVO();
            BeanUtils.copyProperties(saved, toSend);
            toSend.setSelf(false);
            try {
                peerSession.sendMessage(new TextMessage(JSON.toJSONString(toSend)));
            } catch (Exception e) {
                log.error("向接收方推送消息失败，orderId={}, toUserId={}", orderId, toUserId, e);
            }
        } else {
            log.info("接收方当前不在线，orderId={}, toUserId={}", orderId, toUserId);
        }
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
                    // 理论上不会发生（UTF-8 是标准编码），如果发生则记录日志并跳过该参数
                    log.error("URL 解析参数失败，query={}", query, e);
                }
            }
        }
        return map;
    }
}

