public enum TaskState {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE;

    public boolean isValidTransition(TaskState target) {
        switch (this) {
            case PENDING:
                return target == IN_PROGRESS || target == COMPLETED || target == OVERDUE;
            case IN_PROGRESS:
                return target == COMPLETED || target == OVERDUE;
            case COMPLETED:
                return false;
            case OVERDUE:
                return target == COMPLETED;
            default:
                return false;
        }
    }
}
