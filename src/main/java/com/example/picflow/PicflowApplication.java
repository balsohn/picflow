package com.example.picflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PicflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicflowApplication.class, args);
    }

}
