package com.niyiment.proccessor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableBatchProcessing
public class ProccessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProccessorApplication.class, args);
	}

}
