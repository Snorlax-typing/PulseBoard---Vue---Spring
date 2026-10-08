package com.example.demo.ws;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 指标采集与推送调度器。
 * 每 2 秒从 MetricsEngine 取一帧并通过 WebSocket 广播。
 */
@Component
@EnableScheduling
public class MetricsScheduler {

    private final MetricsWebSocketHandler handler;
    private final MetricsEngine engine;
    private final ObjectMapper mapper = new ObjectMapper();

    public MetricsScheduler(MetricsWebSocketHandler handler, MetricsEngine engine) {
        this.handler = handler;
        this.engine = engine;
    }

    @Scheduled(fixedRate = 2000)
    public void push() throws Exception {
        Map<String, Object> snap = new LinkedHashMap<>(engine.advance());
        snap.put("connections", handler.sessionCount());
        handler.broadcast(mapper.writeValueAsString(snap));
    }
}
