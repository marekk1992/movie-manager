package com.example.moviemanager.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsResponse(List<MovieDetails> results) {
}
