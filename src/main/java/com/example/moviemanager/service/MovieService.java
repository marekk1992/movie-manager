package com.example.moviemanager.service;

import com.example.moviemanager.entity.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> findAll();

    Movie findById(int id);

    void deleteById(int id);

    Movie save(Movie movie);
}
