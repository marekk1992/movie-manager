package com.example.moviemanager.controller.model;

import com.example.moviemanager.controller.model.customValidation.validation.OneOfMovieTypes;
import com.example.moviemanager.service.model.CreateMovieInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMovieRequest(
        @NotBlank(message = "Title is mandatory.")
        String title,

        @NotBlank(message = "Movie type is mandatory.")
        @OneOfMovieTypes
        String type,

        @Min(value = 1888, message = "First official movie was released in 1888, please check your input.")
        int releaseYear
) {
        public CreateMovieInfo toCreateMovieInfo() {
                return new CreateMovieInfo(title, type, releaseYear);
        }
}
