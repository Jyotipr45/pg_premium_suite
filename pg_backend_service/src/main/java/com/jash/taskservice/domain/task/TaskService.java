package com.jash.taskservice.domain.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jash.taskservice.core.exception.BusinessRuleException; // 🎯 Correct cross-cutting path!
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 🚪 Tenant opens a new complaint ticket
    public Task createTicket(Task task) {
        task.setStatus(TaskState.PENDING); // Force baseline start state
        return taskRepository.save(task);
    }

    // 📋 Fetch all active tasks for managers
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // ⚙️ Industry-Level State Machine Engine leveraging your custom Enum Guard
    public Task transitionTaskState(Long taskId, TaskState nextState) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task ticket not found with ID: " + taskId));

        TaskState currentState = task.getStatus();

        // 🛡️ Leverage your robust enum transition check!
        if (!currentState.isValidTransition(nextState)) {
            throw new BusinessRuleException("Workflow Error: Cannot transition task from state [" 
                    + currentState + "] to target state [" + nextState + "].");
        }

        // Apply state transition if guards clear
        task.setStatus(nextState);
        return taskRepository.save(task);
    }
}