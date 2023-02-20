package com.example.onlinelibrary.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Configuration
public class WebExternalConfig {

    @Bean
    public RestTemplate externalApiClient(RestTemplateBuilder builder) {
        return builder
                .rootUri("https://restcountries.com/v3.1")
                .setConnectTimeout(Duration.ofSeconds(4))
                .setReadTimeout(Duration.ofSeconds(4))
                .build();
    }
}
