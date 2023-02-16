package com.example.moviemanager.service;

import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieService {

    private final TmdbMovieService tmdbMovieService;
    private final MovieRepository movieRepository;

    public MovieService(TmdbMovieService movieDetailsService, MovieRepository movieRepository) {
        this.tmdbMovieService = movieDetailsService;
        this.movieRepository = movieRepository;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie findById(UUID id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.orElseThrow(() -> new MovieNotFoundException("Could not find movie by id - " + id));
    }

    public void deleteById(UUID id) {
        try {
            movieRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MovieNotFoundException("Deletion failed. Could not find movie with id - " + id);
        }
    }

    public Movie save(FindMovieInfo findMovieInfo) {
        MovieDetails movieDetails = tmdbMovieService.getDetails(findMovieInfo);
        Movie createdMovie = new Movie(
                findMovieInfo.title(),
                movieDetails.description(),
                findMovieInfo.releaseYear(),
                movieDetails.rating()
        );

        return movieRepository.save(createdMovie);
    }

    public Movie update(UUID id, Movie movie) {
        Optional<Movie> tempMovie = movieRepository.findById(id);
        if (tempMovie.isEmpty()) {
            throw new MovieNotFoundException("Update failed. Could not find movie by id - " + id);
        }
        movie.setId(id);

        return movieRepository.save(movie);
    }
}
