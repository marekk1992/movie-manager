package com.example.moviemanager.controller.model;

import com.example.moviemanager.repository.model.Movie;

import java.util.UUID;

public record MovieDetailsResponse(UUID id, String title, String description, int releaseYear, double rating) {

    public static MovieDetailsResponse fromEntity(Movie movie) {
        return new MovieDetailsResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getRating());
    }
}
