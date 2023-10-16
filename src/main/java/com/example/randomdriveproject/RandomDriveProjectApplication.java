package com.example.randomdriveproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RandomDriveProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RandomDriveProjectApplication.class, args);
    }

}
