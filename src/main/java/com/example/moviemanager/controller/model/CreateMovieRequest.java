package com.example.moviemanager.controller.model;

import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMovieRequest(
        @NotBlank(message = "Title is mandatory.")
        String title,

        @NotNull(message = "Movie type is mandatory. Usage: 'MOVIE' or 'TV_SHOW'.")
        MovieType type,

        @Min(value = 1888, message = "First official movie was released in 1888, please check your input.")
        int releaseYear
) {
        public FindMovieInfo toFindMovieInfo() {
                return new FindMovieInfo(title.toUpperCase(), type, releaseYear);
        }
}
