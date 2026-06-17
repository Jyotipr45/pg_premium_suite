package com.jash.taskservice.domain.model;

public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskState status;
    private String username;

    public Task() {
    }

    public Task(Long id, String title, String description, TaskState status, String username) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskState getStatus() { return status; }
    public void setStatus(TaskState status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
