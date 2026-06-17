package com.jash.taskservice.controller;

import com.jash.taskservice.domain.model.Task;
import com.jash.taskservice.repository.TaskRepository;
import com.jash.taskservice.domain.model.TaskState;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAllTasksForUser(Principal principal) {
        String username = principal.getName();
        return taskRepository.findByUsername(username);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, Principal principal) {
        String username = principal.getName();
        task.setUsername(username);
        if (task.getStatus() == null) {
            task.setStatus(TaskState.PENDING);
        }
        return taskRepository.save(task);
    }
}