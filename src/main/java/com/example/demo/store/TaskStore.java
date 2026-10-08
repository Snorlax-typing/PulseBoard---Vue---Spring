package com.example.demo.store;

import com.example.demo.model.Task;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务数据存储层（内存版，模拟数据库）。
 * 在整个系统中：唯一的数据来源，封装数据的增删改查。
 * 被调用场合：由 TaskService 调用；真实项目中可替换为 JPA / MyBatis。
 */
@Repository
public class TaskStore {

    private final Map<Long, Task> db = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    @PostConstruct
    public void seed() {
        long now = System.currentTimeMillis();
        long day = 86_400_000L;
        save(new Task(null, "设计 Vue 仪表盘布局", "high", "in_progress", "小林", now - 3 * day, now + 2 * day));
        save(new Task(null, "搭建 Spring REST API", "high", "done", "阿泽", now - 5 * day, now - 1 * day));
        save(new Task(null, "接入 WebSocket 实时推送", "high", "todo", "阿泽", now - 1 * day, now + 3 * day));
        save(new Task(null, "用 ECharts 画实时折线图", "medium", "in_progress", "小林", now - 2 * day, now + 1 * day));
        save(new Task(null, "实现任务增删改接口", "medium", "done", "阿泽", now - 4 * day, now - 2 * day));
        save(new Task(null, "编写接口联调文档", "low", "todo", "小雨", now, now + 5 * day));
        save(new Task(null, "优化前端响应式布局", "low", "todo", "小林", now - 1 * day, now + 4 * day));
        save(new Task(null, "配置 CORS 与异常处理", "medium", "done", "阿泽", now - 6 * day, now - 3 * day));
    }

    public Task save(Task t) {
        if (t.getId() == null) {
            t.setId(idGen.incrementAndGet());
        }
        db.put(t.getId(), t);
        return t;
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<Task> findAll() {
        List<Task> list = new ArrayList<>(db.values());
        list.sort(Comparator.comparing(Task::getCreatedAt));
        return list;
    }

    public boolean deleteById(Long id) {
        return db.remove(id) != null;
    }
}
