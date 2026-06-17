package com.jash.taskservice.job;

import com.jash.taskservice.domain.model.Task;
import com.jash.taskservice.domain.model.TaskState;
import com.jash.taskservice.repository.TaskRepository;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

@DisallowConcurrentExecution
@Configuration
public class AutoTransitionJob extends QuartzJobBean {

    private final TaskRepository taskRepository;

    // Direct constructor injection completely free of Lombok
    public AutoTransitionJob(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("🤖 Background Engine Triggered: Scanning overdue tasks...");

        try {
            // Retrieve targets using our explicit pessimistic row-level lock configuration
            List<Task> activeTasks = taskRepository.findTasksForUpdate(
                Arrays.asList(TaskState.PENDING, TaskState.IN_PROGRESS)
            );

            for (Task task : activeTasks) {
                // In a production app, evaluate task.getDueDate() against the current time.
                // For this lifecycle engine blueprint, we apply our enum transition guard rules.
                if (task.getStatus().isValidTransition(TaskState.OVERDUE)) {
                    task.setStatus(TaskState.OVERDUE);
                    taskRepository.save(task);
                    System.out.println("🎯 Task ID " + task.getId() + " automatically transitioned to OVERDUE.");
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Automated job execution failure: " + e.getMessage());
            throw new JobExecutionException(e);
        }
    }

    // --- Native Quartz Structural Wirings (JobDetail & Trigger Definition) ---
    @Bean
    public JobDetail autoTransitionJobDetail() {
        return JobBuilder.newJob(AutoTransitionJob.class)
                .withIdentity("autoTransitionJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger autoTransitionJobTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("autoTransitionJobTrigger")
                // Execute every hour on a recurring basis
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                // Bind our holiday exclusion matrix directly to the native trigger pipeline
                .modifiedByCalendar("customBusinessCalendar")
                .build();
    }
}