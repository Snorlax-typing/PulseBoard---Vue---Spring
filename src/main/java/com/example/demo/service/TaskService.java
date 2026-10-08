package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.store.TaskStore;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 任务服务层（业务逻辑）。
 * 在整个系统中：承接 Controller 与 Store，处理业务规则与统计聚合。
 * 被调用场合：由 TaskController 在收到请求时调用。
 */
@Service
public class TaskService {

    private final TaskStore store;

    public TaskService(TaskStore store) {
        this.store = store;
    }

    public List<Task> list(String status, String priority) {
        return store.findAll().stream()
                .filter(t -> status == null || status.isBlank() || status.equals(t.getStatus()))
                .filter(t -> priority == null || priority.isBlank() || priority.equals(t.getPriority()))
                .toList();
    }

    public Task get(Long id) {
        return store.findById(id)
                .orElseThrow(() -> new NoSuchElementException("任务不存在: " + id));
    }

    public Task create(Task t) {
        t.setId(null);
        if (t.getStatus() == null) t.setStatus("todo");
        if (t.getPriority() == null) t.setPriority("medium");
        t.setCreatedAt(System.currentTimeMillis());
        return store.save(t);
    }

    public Task update(Long id, Task patch) {
        Task t = get(id);
        if (patch.getTitle() != null) t.setTitle(patch.getTitle());
        if (patch.getDescription() != null) t.setDescription(patch.getDescription());
        if (patch.getPriority() != null) t.setPriority(patch.getPriority());
        if (patch.getStatus() != null) t.setStatus(patch.getStatus());
        if (patch.getAssignee() != null) t.setAssignee(patch.getAssignee());
        if (patch.getDueAt() != null && patch.getDueAt() > 0) t.setDueAt(patch.getDueAt());
        return store.save(t);
    }

    public boolean delete(Long id) {
        return store.deleteById(id);
    }

    /** 统计聚合：状态分布、优先级分布、完成率等。 */
    public Map<String, Object> stats() {
        List<Task> all = store.findAll();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (String s : List.of("todo", "in_progress", "done")) byStatus.put(s, 0L);
        for (String p : List.of("high", "medium", "low")) byPriority.put(p, 0L);
        for (Task t : all) {
            byStatus.merge(t.getStatus(), 1L, Long::sum);
            byPriority.merge(t.getPriority(), 1L, Long::sum);
        }
        long done = byStatus.get("done");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("total", all.size());
        m.put("byStatus", byStatus);
        m.put("byPriority", byPriority);
        m.put("completionRate", all.isEmpty() ? 0 : Math.round(100.0 * done / all.size()));
        m.put("serverTime", System.currentTimeMillis());
        return m;
    }
}
