package com.example.moviemanager.service.model;

import com.example.moviemanager.repository.model.Movie;

import java.util.UUID;

public record ExistingMovieDetails(UUID id, String title, String description, int releaseYear, double rating) {

    public static ExistingMovieDetails fromEntity(Movie movie) {
        return new ExistingMovieDetails(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getRating());
    }
}
