package com.jash.taskservice.core.config;

import org.quartz.Scheduler;
import org.quartz.impl.calendar.AnnualCalendar;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.util.GregorianCalendar;

@Configuration
public class BusinessCalendarConfig {

    private final Scheduler scheduler;

    public BusinessCalendarConfig(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void registerBusinessCalendar() {
        try {
            // Create a Quartz Annual Calendar to track explicit holiday exclusions
            AnnualCalendar holidayCalendar = new AnnualCalendar();

            // Example 1: Add New Year's Day Exclude
            java.util.Calendar newYearsDay = new GregorianCalendar();
            newYearsDay.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY);
            newYearsDay.set(java.util.Calendar.DAY_OF_MONTH, 1);
            holidayCalendar.setDayExcluded(newYearsDay, true);

            // Example 2: Add Independence Day Exclude (Aug 15)
            java.util.Calendar independenceDay = new GregorianCalendar();
            independenceDay.set(java.util.Calendar.MONTH, java.util.Calendar.AUGUST);
            independenceDay.set(java.util.Calendar.DAY_OF_MONTH, 15);
            holidayCalendar.setDayExcluded(independenceDay, true);

            // Bind our exclusion matrix globally into the persistent quartz engine cache
            scheduler.addCalendar("customBusinessCalendar", holidayCalendar, true, true);

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to initialize Quartz holiday calendar bindings: " + e.getMessage());
        }
    }
}