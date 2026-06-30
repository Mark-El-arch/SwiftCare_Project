package com.swiftcare.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
public class SwiftcareBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftcareBackendApplication.class, args);
	}

	@SpringBootApplication
	@EnableScheduling
	public class SwiftCareBackendApplication {
		public static void main(String[] args) {
			SpringApplication.run(SwiftCareBackendApplication.class, args);
		}
	}
}
