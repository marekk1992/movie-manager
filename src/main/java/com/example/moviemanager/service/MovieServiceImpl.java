package com.example.moviemanager.service;

import com.example.moviemanager.entity.Movie;
import com.example.moviemanager.repo.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public Movie findById(int id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isEmpty()) {
            throw new MovieNotFoundException("Could not find Movie by id - " + id);
        }

        return movie.get();
    }

    @Override
    public void deleteById(int id) {
        try {
            movieRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MovieNotFoundException("Could not find Movie by id - " + id);
        }
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }
}
