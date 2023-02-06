package com.example.moviemanager.controller;

import com.example.moviemanager.controller.dto.CreateMovieRequest;
import com.example.moviemanager.controller.dto.UpdateMovieRequest;
import com.example.moviemanager.controller.dto.MovieResponse;
import com.example.moviemanager.controller.dto.MovieCollectionResponse;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public MovieCollectionResponse findAll() {
        List<Movie> movies = movieService.findAll();
        return MovieCollectionResponse.fromEntity(movies);
    }

    @GetMapping("/{movieId}")
    public MovieResponse findById(@PathVariable UUID movieId) {
        Movie movie = movieService.findById(movieId);
        return MovieResponse.fromEntity(movie);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse addMovie(@Valid @RequestBody CreateMovieRequest request) {
        Movie movie = movieService.save(request.toEntity());
        return MovieResponse.fromEntity(movie);
    }

    @PutMapping("/{movieId}")
    public MovieResponse updateMovie(@Valid @RequestBody UpdateMovieRequest request, @PathVariable UUID movieId) {
        Movie movie = movieService.update(movieId, request.toEntity());
        return MovieResponse.fromEntity(movie);
    }

    @DeleteMapping("/{movieId}")
    public void deleteMovie(@PathVariable UUID movieId) {
        movieService.deleteById(movieId);
    }
}
