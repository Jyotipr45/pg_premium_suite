package com.jash.taskservice.domain.task;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // 🛡️ TENANT ONLY - File an issue
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Task> createTicket(@RequestBody Task task) {
        Task createdTask = taskService.createTicket(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // 👥 ADMIN & OWNER ONLY - Track operational boards
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // 🛡️ ADMIN & OWNER ONLY - Advance the state lifecycle
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestParam TaskState targetState) {
        Task updatedTask = taskService.transitionTaskState(id, targetState);
        return ResponseEntity.ok(updatedTask);
    }
}