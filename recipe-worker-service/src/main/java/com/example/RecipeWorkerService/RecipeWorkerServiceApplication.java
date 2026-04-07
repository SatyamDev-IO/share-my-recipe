package com.example.RecipeWorkerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class RecipeWorkerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeWorkerServiceApplication.class, args);
	}

}
