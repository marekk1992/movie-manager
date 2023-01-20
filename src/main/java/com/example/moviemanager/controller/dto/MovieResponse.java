package com.example.moviemanager.controller.dto;

import com.example.moviemanager.repository.model.Movie;

public record MovieResponse(Integer id, String title, String description, int releaseYear, double rating) {

    public static MovieResponse fromEntity(Movie movie) {
        return new MovieResponse(movie.getId(), movie.getTitle(), movie.getDescription(), movie.getReleaseYear(),
                movie.getRating());
    }
}
