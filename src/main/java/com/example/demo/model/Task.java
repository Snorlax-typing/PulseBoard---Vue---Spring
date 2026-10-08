package com.example.demo.model;

/**
 * 任务实体（领域模型）。
 * 在整个系统中：前后端交互的数据载体，经 Jackson 自动序列化为 JSON。
 */
public class Task {

    private Long id;
    private String title;
    private String description;
    private String priority;   // high / medium / low
    private String status;     // todo / in_progress / done
    private String assignee;
    private Long createdAt;
    private Long dueAt;

    public Task() {}

    public Task(Long id, String title, String priority, String status,
                String assignee, Long createdAt, Long dueAt) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
        this.createdAt = createdAt;
        this.dueAt = dueAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getDueAt() { return dueAt; }
    public void setDueAt(Long dueAt) { this.dueAt = dueAt; }
}
