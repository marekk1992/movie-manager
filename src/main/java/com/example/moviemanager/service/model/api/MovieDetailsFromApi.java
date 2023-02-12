package com.example.moviemanager.service.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsFromApi(String overview, double vote_average) {
}
