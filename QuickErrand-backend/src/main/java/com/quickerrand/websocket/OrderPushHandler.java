package com.quickerrand.websocket;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单推送WebSocket处理器
 *
 * @author 周政
 * @date 2026-01-26
 */
@Slf4j
@Component
public class OrderPushHandler extends TextWebSocketHandler {

    /**
     * 存储跑腿员的WebSocket会话
     * key: 跑腿员ID, value: WebSocketSession
     */
    private static final Map<Long, WebSocketSession> RUNNER_SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取跑腿员ID
        String query = session.getUri().getQuery();
        if (query != null && query.contains("runnerId=")) {
            String runnerIdStr = query.split("runnerId=")[1].split("&")[0];
            Long runnerId = Long.parseLong(runnerIdStr);
            RUNNER_SESSIONS.put(runnerId, session);
            log.info("跑腿员{}建立WebSocket连接", runnerId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除断开连接的跑腿员
        RUNNER_SESSIONS.entrySet().removeIf(entry -> entry.getValue().equals(session));
        log.info("WebSocket连接关闭");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 处理客户端发送的消息（如心跳包）
        log.debug("收到WebSocket消息: {}", message.getPayload());
    }

    /**
     * 推送新订单给所有在线的跑腿员
     *
     * @param orderData 订单数据
     */
    public void pushNewOrder(Object orderData) {
        String message = JSON.toJSONString(orderData);
        RUNNER_SESSIONS.forEach((runnerId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                    log.info("推送新订单给跑腿员{}", runnerId);
                } catch (IOException e) {
                    log.error("推送订单失败，跑腿员ID: {}", runnerId, e);
                }
            }
        });
    }

    /**
     * 推送订单给指定跑腿员
     *
     * @param runnerId 跑腿员ID
     * @param orderData 订单数据
     */
    public void pushOrderToRunner(Long runnerId, Object orderData) {
        WebSocketSession session = RUNNER_SESSIONS.get(runnerId);
        if (session != null && session.isOpen()) {
            try {
                String message = JSON.toJSONString(orderData);
                session.sendMessage(new TextMessage(message));
                log.info("推送订单给跑腿员{}", runnerId);
            } catch (IOException e) {
                log.error("推送订单失败，跑腿员ID: {}", runnerId, e);
            }
        }
    }

}
