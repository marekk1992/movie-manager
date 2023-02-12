package com.example.moviemanager.service.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(List<MovieDetailsFromApi> results) {
}
