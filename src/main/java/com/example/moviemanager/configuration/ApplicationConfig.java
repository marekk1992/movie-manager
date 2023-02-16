package com.example.moviemanager.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate(TmdbConfigProperties configProperties) {
        return new RestTemplateBuilder().rootUri(configProperties.getBaseUrl()).build();
    }
}
