package com.jash.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TaskserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskserviceApplication.class, args);
	}

}
