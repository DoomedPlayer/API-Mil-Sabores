package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.example.test.model")
public class TestApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(TestApplication.class, args);
	}

}

//http://localhost:8080/ link postman
