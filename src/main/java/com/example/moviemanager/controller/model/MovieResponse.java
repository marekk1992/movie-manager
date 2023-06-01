package com.example.moviemanager.controller.model;

import com.example.moviemanager.repository.model.Movie;

public record MovieResponse(String title, int releaseYear, double rating) {

    public static MovieResponse fromEntity(Movie movie) {
        return new MovieResponse(
                movie.getTitle(),
                movie.getReleaseYear(),
                movie.getRating());
    }
}
