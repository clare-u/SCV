package com.scv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScvApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScvApplication.class, args);
    }

}
