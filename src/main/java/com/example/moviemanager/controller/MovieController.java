package com.example.moviemanager.controller;

import com.example.moviemanager.repository.Movie;
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

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> findAll() {
        return movieService.findAll();
    }

    @GetMapping("/{movieId}")
    public Movie findById(@PathVariable int movieId) {
        return movieService.findById(movieId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movie addMovie(@Valid @RequestBody Movie movie) {
        return movieService.save(movie);
    }

    @PutMapping("/{movieId}")
    public Movie updateMovie(@Valid @PathVariable int movieId, @RequestBody Movie movie) {
        return movieService.update(movieId, movie);
    }

    @DeleteMapping("/{movieId}")
    public void deleteMovie(@PathVariable int movieId) {
        movieService.deleteById(movieId);
    }
}
