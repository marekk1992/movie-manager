package com.example.moviemanager.controller.model;

import com.example.moviemanager.repository.model.Movie;

import java.util.UUID;

public record MovieResponse(UUID id, String title, String description, int releaseYear, double rating) {

    public static MovieResponse fromEntity(Movie movie) {
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getRating());
    }
}
