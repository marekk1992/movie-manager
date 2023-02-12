package com.example.moviemanager.controller.model;

import com.example.moviemanager.service.model.ExistingMovieDetails;

import java.util.UUID;

public record MovieResponse(UUID id, String title, String description, int releaseYear, double rating) {

    public static MovieResponse fromExistingMovieDetails(ExistingMovieDetails existingMovieDetails) {
        return new MovieResponse(
                existingMovieDetails.id(),
                existingMovieDetails.title(),
                existingMovieDetails.description(),
                existingMovieDetails.releaseYear(),
                existingMovieDetails.rating());
    }
}
