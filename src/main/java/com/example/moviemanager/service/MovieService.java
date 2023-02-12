package com.example.moviemanager.service;

import com.example.moviemanager.constantValues.MovieType;
import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.exception.FailedToRetrieveDataFromDatabaseException;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.service.exception.UniqueMovieNotFoundException;
import com.example.moviemanager.service.model.CreateMovieInfo;
import com.example.moviemanager.service.model.ExistingMovieDetails;
import com.example.moviemanager.service.model.UpdateMovieInfo;
import com.example.moviemanager.service.model.api.ApiResponse;
import com.example.moviemanager.service.model.api.MovieDetailsFromApi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private static final String TV_SHOWS_URI =
            "https://api.themoviedb.org/3/search/tv?api_key=77938ea875914a4c8ded6ccb9d840d27&query=";
    private static final String MOVIES_URI =
            "https://api.themoviedb.org/3/search/movie?api_key=77938ea875914a4c8ded6ccb9d840d27&query=";
    private static final String TV_SHOW_RELEASE_YEAR = "&first_air_date_year=";
    private static final String MOVIE_RELEASE_YEAR = "&year=";

    private final MovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<ExistingMovieDetails> findAll() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(ExistingMovieDetails::fromEntity)
                .collect(Collectors.toList());
    }

    public ExistingMovieDetails findById(UUID id) {
        Optional<Movie> movie = movieRepository.findById(id);
        Movie foundMovie = movie.orElseThrow(() -> new MovieNotFoundException("Could not find movie by id - " + id));

        return ExistingMovieDetails.fromEntity(foundMovie);
    }

    public void deleteById(UUID id) {
        try {
            movieRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new MovieNotFoundException("Deletion failed. Could not find movie with id - " + id);
        }
    }

    public ExistingMovieDetails save(CreateMovieInfo createMovieInfo) {
        MovieDetailsFromApi movieDetailsFromApi = getDetailsFromMovieDatabase(createMovieInfo);
        Movie createdMovie = movieRepository.save(buildMovie(createMovieInfo, movieDetailsFromApi));

        return ExistingMovieDetails.fromEntity(createdMovie);
    }

    public ExistingMovieDetails update(UUID id, UpdateMovieInfo updateMovieInfo) {
        Optional<Movie> tempMovie = movieRepository.findById(id);
        if (tempMovie.isEmpty()) {
            throw new MovieNotFoundException("Update failed. Could not find movie by id - " + id);
        }

        Movie movieWithNewDetails = updateMovieInfo.toEntity();
        movieWithNewDetails.setId(id);
        Movie updatedMovie = movieRepository.save(movieWithNewDetails);

        return ExistingMovieDetails.fromEntity(updatedMovie);
    }

    private MovieDetailsFromApi getDetailsFromMovieDatabase(CreateMovieInfo createMovieInfo) {
        String uri = buildUri(createMovieInfo);
        ApiResponse apiResponse = getResponseFromMovieDatabase(uri);

        return apiResponse.results().get(0);
    }

    private Movie buildMovie(CreateMovieInfo saveMovieRequest, MovieDetailsFromApi movieDetailsFromApi) {
        return new Movie(
                saveMovieRequest.title().toUpperCase(),
                movieDetailsFromApi.overview(),
                saveMovieRequest.releaseYear(),
                movieDetailsFromApi.vote_average()
        );
    }

    private String buildUri(CreateMovieInfo saveMovieRequest) {
        String movieType = saveMovieRequest.type();
        if (movieType.equalsIgnoreCase(MovieType.MOVIE.toString())) {
            return MOVIES_URI + saveMovieRequest.title() + MOVIE_RELEASE_YEAR + saveMovieRequest.releaseYear();
        }
        return TV_SHOWS_URI + saveMovieRequest.title() + TV_SHOW_RELEASE_YEAR + saveMovieRequest.releaseYear();
    }

    private ApiResponse getResponseFromMovieDatabase(String uri) {
        try {
            ApiResponse apiResponse = objectMapper.readValue(
                    new RestTemplate().getForObject(uri, String.class),
                    ApiResponse.class
            );
            if(apiResponse.results().size() != 1) {
                throw new UniqueMovieNotFoundException("Can`t find unique movie according to your details.");
            }
            return apiResponse;
        } catch (JsonProcessingException e) {
            throw new FailedToRetrieveDataFromDatabaseException("Failed to retrieve data from database.");
        }
    }
}
