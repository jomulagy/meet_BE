package com.example.meet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetApplication.class, args);
	}
}
