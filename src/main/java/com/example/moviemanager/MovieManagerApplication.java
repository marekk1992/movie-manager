package com.example.moviemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MovieManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieManagerApplication.class, args);
    }
}
