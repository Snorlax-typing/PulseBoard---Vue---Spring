package com.example.demo.web;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务 REST 控制器（API 层）。
 * 在整个系统中：Vue 前端通过 HTTP 调用这些接口，是前后端的"契约边界"。
 * 被调用场合：前端 fetch / axios 发起请求时，由 DispatcherServlet 路由到这里。
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    /** 列表查询，支持 ?status= &priority= 过滤 */
    @GetMapping
    public List<Task> list(@RequestParam(required = false) String status,
                           @RequestParam(required = false) String priority) {
        return service.list(status, priority);
    }

    /** 统计看板数据 */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.stats();
    }

    /** 单个任务 */
    @GetMapping("/{id}")
    public Task get(@PathVariable Long id) {
        return service.get(id);
    }

    /** 新建任务 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@RequestBody Task t) {
        return service.create(t);
    }

    /** 更新任务（含状态切换） */
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task t) {
        return service.update(id, t);
    }

    /** 删除任务 */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        return Map.of("success", ok);
    }
}
