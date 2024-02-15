package com.mobiauto.backendinterview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
@SpringBootApplication(scanBasePackages = {"config", "component", "controller", "enums", "exceptionhandler", "service", "service.impl", "messages", "dto"})
public class MobiautoChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobiautoChallengeApplication.class, args);
	}
}