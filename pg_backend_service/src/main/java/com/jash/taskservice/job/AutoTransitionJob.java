package com.jash.taskservice.job;

import com.jash.taskservice.domain.model.Task;
import com.jash.taskservice.domain.model.TaskState;
import com.jash.taskservice.repository.TaskRepository;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DisallowConcurrentExecution
@Configuration
public class AutoTransitionJob extends QuartzJobBean {

    private final TaskRepository taskRepository;

    public AutoTransitionJob(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("🤖 SLA Automation Engine: Auditing active PG maintenance tickets...");

        try {
            // Retrieve targets using our explicit pessimistic row-level lock configuration
            List<Task> activeTickets = taskRepository.findTasksForUpdate(
                Arrays.asList(TaskState.PENDING, TaskState.IN_PROGRESS)
            );

            LocalDateTime now = LocalDateTime.now();

            for (Task ticket : activeTickets) {
                // If the maintenance ticket has surpassed its SLA due date, transition its state
                if (ticket.getDueDate() != null && now.isAfter(ticket.getDueDate())) {
                    if (ticket.getStatus().isValidTransition(TaskState.OVERDUE)) {
                        ticket.setStatus(TaskState.OVERDUE);
                        taskRepository.save(ticket);
                        System.out.println("🎯 Maintenance Ticket ID " + ticket.getId() + 
                                           " for Room " + ticket.getRoomNumber() + " breached SLA! Flagged as OVERDUE.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Maintenance SLA automation job failure: " + e.getMessage());
            throw new JobExecutionException(e);
        }
    }

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
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                .modifiedByCalendar("customBusinessCalendar")
                .build();
    }
}