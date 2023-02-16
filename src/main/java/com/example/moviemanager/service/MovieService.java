package com.example.moviemanager.service;

import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.tmdbmovieservice.TmdbMovieService;
import com.example.moviemanager.service.tmdbmovieservice.model.FindMovieInfo;
import com.example.moviemanager.service.tmdbmovieservice.model.MovieDetails;
import com.example.moviemanager.service.movieservice.exception.MovieNotFoundException;
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
        return movieRepository.save(tmdbMovieService.getMovie(findMovieInfo));
    }

    public Movie update(UUID id, Movie movie) {
        Optional<Movie> tempMovie = movieRepository.findById(id);
        if (tempMovie.isEmpty()) {
            throw new MovieNotFoundException("Update failed. Could not find movie by id - " + id);
        }
        movie.setId(id);

        return movieRepository.save(movie);
    }

    private Movie buildMovie(FindMovieInfo findMovieInfo, MovieDetails movieDetails) {
        return new Movie(
                findMovieInfo.title(),
                movieDetails.overview(),
                findMovieInfo.releaseYear(),
                movieDetails.vote_average()
        );
    }
}
