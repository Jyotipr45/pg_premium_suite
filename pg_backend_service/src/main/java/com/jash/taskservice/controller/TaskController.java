package com.jash.taskservice.controller;

import com.jash.taskservice.domain.exception.BusinessRuleException;
import com.jash.taskservice.domain.model.Task;
import com.jash.taskservice.domain.model.TaskState;
import com.jash.taskservice.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 1. Create a New Maintenance Ticket (Bounded to authenticated Resident JWT)
    @PostMapping
    public ResponseEntity<Task> createTicket(@RequestBody Task incomingPayload, Principal principal) {
        Task ticket = new Task();
        ticket.setUsername(principal.getName()); // Extracted securely from token context
        ticket.setTitle(incomingPayload.getTitle());
        ticket.setDescription(incomingPayload.getDescription());
        ticket.setRoomNumber(incomingPayload.getRoomNumber());
        ticket.setStatus(TaskState.PENDING);
        
        // Default SLA: 24 hours from now to resolve the issue
        ticket.setDueDate(LocalDateTime.now().plusHours(24));

        Task savedTicket = taskRepository.save(ticket);
        return new ResponseEntity<>(savedTicket, HttpStatus.CREATED);
    }

    // 2. Retrieve All Maintenance Tickets belonging exclusively to the logged-in resident
    @GetMapping
    public ResponseEntity<List<Task>> getMyTickets(Principal principal) {
        List<Task> tickets = taskRepository.findByUsername(principal.getName());
        return ResponseEntity.ok(tickets);
    }

    // 3. Update Ticket Status (Technician assigns to IN_PROGRESS or marks COMPLETED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam TaskState targetStatus,
            Principal principal) {
            
        Task ticket = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Maintenance ticket not found with ID: " + id));

        // State Machine Guard Verification Pass
        if (!ticket.getStatus().isValidTransition(targetStatus)) {
            throw new BusinessRuleException("Invalid status transition from " + ticket.getStatus() + " to " + targetStatus);
        }

        ticket.setStatus(targetStatus);
        Task updatedTicket = taskRepository.save(ticket);
        return ResponseEntity.ok(updatedTicket);
    }
}