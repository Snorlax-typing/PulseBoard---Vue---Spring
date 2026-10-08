package com.example.demo.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实时指标 WebSocket 处理器。
 * 维护所有已连接会话；新连接建立时立即发一帧，之后由调度器定时广播。
 */
@Component
public class MetricsWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final MetricsEngine engine;
    private final ObjectMapper mapper = new ObjectMapper();

    public MetricsWebSocketHandler(MetricsEngine engine) {
        this.engine = engine;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        // 立即推送当前快照，前端无需等待首个调度周期
        try {
            Map<String, Object> snap = new LinkedHashMap<>(engine.peek());
            snap.put("connections", sessions.size());
            session.sendMessage(new TextMessage(mapper.writeValueAsString(snap)));
        } catch (IOException e) {
            sessions.remove(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public int sessionCount() {
        return sessions.size();
    }

    /** 向所有连接的客户端广播一条 JSON 文本。 */
    public void broadcast(String json) {
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (!s.isOpen()) continue;
            try {
                synchronized (s) {
                    s.sendMessage(msg);
                }
            } catch (IOException e) {
                sessions.remove(s);
            }
        }
    }
}
