package com.example.moviemanager.controller.dto;

import com.example.moviemanager.repository.model.Movie;

import java.util.List;
import java.util.stream.Collectors;

public record MoviesResponse(List<MovieResponse> movies) {

    public static MoviesResponse fromEntity(List<Movie> movies) {
        List<MovieResponse> responses = movies.stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());

        return new MoviesResponse(responses);
    }
}
