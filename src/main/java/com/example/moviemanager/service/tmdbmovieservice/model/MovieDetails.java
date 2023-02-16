package com.example.moviemanager.service.tmdbmovieservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetails(String overview, double vote_average) {
}
