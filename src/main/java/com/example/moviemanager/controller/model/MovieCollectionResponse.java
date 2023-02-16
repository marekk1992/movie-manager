package com.example.moviemanager.controller.model;

import com.example.moviemanager.repository.model.Movie;

import java.util.List;
import java.util.stream.Collectors;

public record MovieCollectionResponse(List<MovieResponse> movies) {

    public static MovieCollectionResponse fromEntity(List<Movie> movies) {
        List<MovieResponse> responses = movies.stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());

        return new MovieCollectionResponse(responses);
    }
}
