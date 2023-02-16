package com.example.moviemanager.service;

import com.example.moviemanager.configuration.TmdbConfigProperties;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.service.exception.UniqueMovieDetailsNotFoundException;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import com.example.moviemanager.service.model.MovieType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbMovieService {

    private static final String PATH = "/3/search/";
    private static final String QUERY = "query";
    private static final String API_KEY = "api_key";
    private static final String MOVIE_TYPE = "movie";
    private static final String TV_SHOW_TYPE = "tv";
    private static final String MOVIE_RELEASE_YEAR = "year";
    private static final String TV_SHOW_RELEASE_YEAR = "first_air_date_year";

    private final TmdbConfigProperties tmdbConfigProperties;
    private final RestTemplate restTemplate;

    public TmdbMovieService(TmdbConfigProperties tmdbConfigProperties, RestTemplate restTemplate) {
        this.tmdbConfigProperties = tmdbConfigProperties;
        this.restTemplate = restTemplate;
    }

    public MovieDetails getDetails(FindMovieInfo findMovieInfo) {
        String endpoint = buildUri(findMovieInfo);
        MovieDetailsResponse movieDetailsResponse = getResponse(endpoint);
        return retrieveMovieDetails(movieDetailsResponse);
    }

    private String buildUri(FindMovieInfo findMovieInfo) {
        MovieType movieType = findMovieInfo.type();
        String pathComponent = movieType.equals(MovieType.MOVIE) ? MOVIE_TYPE : TV_SHOW_TYPE;
        String releaseYear = movieType.equals(MovieType.MOVIE) ? MOVIE_RELEASE_YEAR : TV_SHOW_RELEASE_YEAR;

        return UriComponentsBuilder
                .newInstance()
                .path(PATH + pathComponent)
                .queryParam(API_KEY, tmdbConfigProperties.getApiKey())
                .queryParam(QUERY, findMovieInfo.title())
                .queryParam(releaseYear, findMovieInfo.releaseYear())
                .build()
                .toUriString();
    }

    private MovieDetailsResponse getResponse(String endpoint) {
        MovieDetailsResponse movieDetailsResponse = restTemplate.getForObject(endpoint, MovieDetailsResponse.class);
        if (movieDetailsResponse == null) {
            throw new MovieNotFoundException("Can`t find any movie according to your request.");
        }
        return movieDetailsResponse;
    }

    private MovieDetails retrieveMovieDetails(MovieDetailsResponse movieDetailsResponse) {
        if (movieDetailsResponse.results() == null || movieDetailsResponse.results().size() != 1) {
            throw new UniqueMovieDetailsNotFoundException("Can`t find unique movie according to your request.");
        }
        return movieDetailsResponse.results().get(0);
    }

    private Movie buildMovie(FindMovieInfo findMovieInfo, MovieDetails movieDetails) {
        return new Movie(
                findMovieInfo.title(),
                movieDetails.description(),
                findMovieInfo.releaseYear(),
                movieDetails.rating()
        );
    }
}
