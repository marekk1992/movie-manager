package com.example.moviemanager.service;

import com.example.moviemanager.repository.Movie;
import com.example.moviemanager.repository.MovieRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie findById(int id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.orElseThrow(() -> new MovieNotFoundException("Could not find movie by id - " + id));
    }

    public void deleteById(int id) {
        try {
            movieRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MovieNotFoundException("Deletion failed. Could not find movie with id - " + id);
        }
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie update(int id, Movie movie) {
        Optional<Movie> tempMovie = movieRepository.findById(id);
        if (tempMovie.isEmpty()) {
            throw new MovieNotFoundException("Update failed. Could not find movie by id - " + id);
        }
        movie.setId(id);

        return movieRepository.save(movie);
    }
}