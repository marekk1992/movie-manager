package com.example.moviemanager.controller.model;

import com.example.moviemanager.service.model.ExistingMovieDetails;

import java.util.List;
import java.util.stream.Collectors;

public record MovieCollectionResponse(List<MovieResponse> movies) {

    public static MovieCollectionResponse fromExistingMovieDetails(List<ExistingMovieDetails> movieDetails) {
        List<MovieResponse> responses = movieDetails.stream()
                .map(MovieResponse::fromExistingMovieDetails)
                .collect(Collectors.toList());

        return new MovieCollectionResponse(responses);
    }
}
