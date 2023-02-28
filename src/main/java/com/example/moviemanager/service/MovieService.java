package com.example.moviemanager.service;

import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.service.exception.UniqueMovieDetailsNotFoundException;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;

    public MovieService(TmdbClient movieDetailsService, MovieRepository movieRepository) {
        this.tmdbClient = movieDetailsService;
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
        MovieDetailsResponse movieDetailsResponse = tmdbClient.find(findMovieInfo);
        MovieDetails movieDetails = retrieveMovieDetails(movieDetailsResponse);
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

    private MovieDetails retrieveMovieDetails(MovieDetailsResponse movieDetailsResponse) {
        if (movieDetailsResponse.results() == null || movieDetailsResponse.results().size() != 1) {
            throw new UniqueMovieDetailsNotFoundException("Can`t find unique movie according to your request.");
        }
        return movieDetailsResponse.results().get(0);
    }
}
