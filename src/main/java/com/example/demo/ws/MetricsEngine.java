package com.example.demo.ws;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 指标生成引擎（内存状态）。
 * 负责用"随机游走"生成逼真的服务器指标；调度器与连接处理器都从这里取数。
 */
@Component
public class MetricsEngine {

    private double cpu = 32;
    private double mem = 54;
    private double rps = 12;
    private final AtomicLong totalRequests = new AtomicLong(10000);

    /** 推进一步并返回当前快照（用于定时推送）。 */
    public synchronized Map<String, Object> advance() {
        cpu = clamp(cpu + (Math.random() * 22 - 10), 8, 95);
        mem = clamp(mem + (Math.random() * 8 - 4), 30, 92);
        rps = clamp(rps + (Math.random() * 16 - 8), 2, 80);
        totalRequests.addAndGet(Math.round(rps * 2));
        return snapshot();
    }

    /** 不推进，只返回当前快照（用于新连接建立时立即发一帧）。 */
    public synchronized Map<String, Object> peek() {
        return snapshot();
    }

    private Map<String, Object> snapshot() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("time", System.currentTimeMillis());
        m.put("cpu", Math.round(cpu * 10) / 10.0);
        m.put("memory", Math.round(mem * 10) / 10.0);
        m.put("rps", Math.round(rps));
        m.put("totalRequests", totalRequests.get());
        return m;
    }

    private double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
