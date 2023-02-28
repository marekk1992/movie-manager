package com.example.moviemanager.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tmdb")
public record TmdbConfigProperties(String baseUrl, String apiKey) {
}
