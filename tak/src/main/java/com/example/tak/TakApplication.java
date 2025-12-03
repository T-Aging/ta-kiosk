package com.example.tak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class TakApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakApplication.class, args);
	}

}
