package com.example.moviemanager.service.model;

import com.example.moviemanager.repository.model.Movie;

public record UpdateMovieInfo(String title, String description, int releaseYear, double rating) {

    public Movie toEntity() {
        return new Movie(title, description, releaseYear, rating);
    }
}
