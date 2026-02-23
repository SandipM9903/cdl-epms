package com.cdl.epms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EpmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpmsApplication.class, args);
	}

}
