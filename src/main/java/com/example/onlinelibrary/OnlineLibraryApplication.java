package com.example.onlinelibrary;

import com.example.onlinelibrary.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfiguration.class)
public class OnlineLibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineLibraryApplication.class, args);
    }
}
