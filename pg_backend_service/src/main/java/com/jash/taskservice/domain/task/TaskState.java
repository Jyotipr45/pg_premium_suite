package com.jash.taskservice.domain.task;

public enum TaskState {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE;

    public boolean isValidTransition(TaskState target) {
        if (this == COMPLETED) {
            return false; // Completed tasks cannot change state
        }
        if (this == PENDING && target == OVERDUE) {
            return true; // Automated fallback
        }
        if (this == IN_PROGRESS && (target == COMPLETED || target == OVERDUE)) {
            return true;
        }
        return this == PENDING && target == IN_PROGRESS;
    }
}