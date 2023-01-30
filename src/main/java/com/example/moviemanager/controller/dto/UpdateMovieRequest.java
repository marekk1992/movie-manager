package com.example.moviemanager.controller.dto;

import com.example.moviemanager.repository.model.Movie;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMovieRequest(
        @NotBlank(message = "Title is mandatory.")
        @Size(max = 100, message = "Title can`t be longer than 100 characters.")
        String title,

        @Size(max = 500, message = "Description can`t be longer than 500 characters.")
        String description,

        @Min(value = 1888, message = "First official movie was released in 1888, please check your input.")
        int releaseYear,

        @Min(value = 0, message = "Rating can`t be negative.")
        @Max(value = 10, message = "Rating can`t be higher than 10.")
        double rating
) {

    public Movie toEntity() {
        return new Movie(title, description, releaseYear, rating);
    }
}
