package com.example.onlinelibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OnlineLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineLibraryApplication.class, args);
    }

}
