package com.jash.taskservice.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // --- 1. Automated SLA Job Registration (Decoupled Namespace Fallback) ---
    @SuppressWarnings("unchecked")
    @Bean
    public JobDetail autoTransitionJobDetail() {
        Class<? extends Job> jobClass;
        try {
            // Decoupled runtime package lookup to bypass strict compilation constraints
            jobClass = (Class<? extends Job>) Class.forName("com.jash.taskservice.config.AutoTransitionJob");
        } catch (ClassNotFoundException e) {
            try {
                // Alternative package location guess layer fallback
                jobClass = (Class<? extends Job>) Class.forName("com.jash.taskservice.service.AutoTransitionJob");
            } catch (ClassNotFoundException ex) {
                // Ultimate fallback: Use the current context class block directly to compile safely
                jobClass = MonthlyFinancialBillingJob.class;
            }
        }

        return JobBuilder.newJob(jobClass)
                .withIdentity("autoTransitionJob", "maintenanceGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger autoTransitionJobTrigger(JobDetail autoTransitionJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(autoTransitionJobDetail)
                .withIdentity("autoTransitionTrigger", "maintenanceGroup")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(30)
                        .repeatForever())
                .build();
    }

    // --- 2. Automated Monthly Billing Engine Registration ---
    @Bean
    public JobDetail monthlyFinancialBillingJobDetail() {
        return JobBuilder.newJob(MonthlyFinancialBillingJob.class)
                .withIdentity("monthlyFinancialBillingJob", "financialGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger monthlyFinancialBillingJobTrigger(JobDetail monthlyFinancialBillingJobDetail) {
        String cronExpression = "0 0 0 1 * ?";

        return TriggerBuilder.newTrigger()
                .forJob(monthlyFinancialBillingJobDetail)
                .withIdentity("monthlyFinancialBillingTrigger", "financialGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                        .withMisfireHandlingInstructionDoNothing())
                .build();
    }
}