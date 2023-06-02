package com.example.moviemanager.controller;

import com.example.moviemanager.controller.model.CreateMovieRequest;
import com.example.moviemanager.controller.model.MovieCollectionResponse;
import com.example.moviemanager.controller.model.MovieDetailsResponse;
import com.example.moviemanager.controller.model.UpdateMovieRequest;
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

import java.util.UUID;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public MovieCollectionResponse findAll() {
        return MovieCollectionResponse.fromEntity(movieService.findAll());
    }

    @GetMapping("/{movieId}")
    public MovieDetailsResponse findById(@PathVariable UUID movieId) {
        return MovieDetailsResponse.fromEntity(movieService.findById(movieId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDetailsResponse addMovie(@Valid @RequestBody CreateMovieRequest request) {
        return MovieDetailsResponse.fromEntity(movieService.save(request.toFindMovieInfo()));
    }

    @PutMapping("/{movieId}")
    public MovieDetailsResponse updateMovie(@Valid @RequestBody UpdateMovieRequest request, @PathVariable UUID movieId) {
        return MovieDetailsResponse.fromEntity(movieService.update(movieId, request.toEntity()));
    }

    @DeleteMapping("/{movieId}")
    public void deleteMovie(@PathVariable UUID movieId) {
        movieService.deleteById(movieId);
    }
}
