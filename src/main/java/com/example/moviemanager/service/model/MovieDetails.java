package com.example.moviemanager.service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetails(@JsonProperty("overview") String description, @JsonProperty("vote_average") double rating) {
}
