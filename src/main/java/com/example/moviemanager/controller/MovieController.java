package com.example.moviemanager.controller;

import com.example.moviemanager.controller.model.CreateMovieRequest;
import com.example.moviemanager.controller.model.MovieCollectionResponse;
import com.example.moviemanager.controller.model.MovieResponse;
import com.example.moviemanager.controller.model.UpdateMovieRequest;
import com.example.moviemanager.service.MovieService;
import com.example.moviemanager.service.model.ExistingMovieDetails;
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
        List<ExistingMovieDetails> existingMoviesDetails = movieService.findAll();
        return MovieCollectionResponse.fromExistingMovieDetails(existingMoviesDetails);
    }

    @GetMapping("/{movieId}")
    public MovieResponse findById(@PathVariable UUID movieId) {
        ExistingMovieDetails existingMovieDetails = movieService.findById(movieId);
        return MovieResponse.fromExistingMovieDetails(existingMovieDetails);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse addMovie(@Valid @RequestBody CreateMovieRequest request) {
        ExistingMovieDetails existingMovieDetails = movieService.save(request.toCreateMovieInfo());
        return MovieResponse.fromExistingMovieDetails(existingMovieDetails);
    }

    @PutMapping("/{movieId}")
    public MovieResponse updateMovie(@Valid @RequestBody UpdateMovieRequest request, @PathVariable UUID movieId) {
        ExistingMovieDetails existingMovieDetails = movieService.update(movieId, request.toUpdateMovieDetails());
        return MovieResponse.fromExistingMovieDetails(existingMovieDetails);
    }

    @DeleteMapping("/{movieId}")
    public void deleteMovie(@PathVariable UUID movieId) {
        movieService.deleteById(movieId);
    }
}
