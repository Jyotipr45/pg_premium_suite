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
        if (task.getStatus() == null) {
            task.setStatus(TaskState.PENDING);
        }
        task.setUsername(username);
        return taskRepository.save(task);
    }
}
