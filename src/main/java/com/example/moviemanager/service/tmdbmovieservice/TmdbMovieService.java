package com.example.moviemanager.service.tmdbmovieservice;

import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.tmdbmovieservice.configuration.ConfigProperties;
import com.example.moviemanager.service.tmdbmovieservice.exception.UniqueMovieDetailsNotFoundException;
import com.example.moviemanager.service.tmdbmovieservice.model.FindMovieInfo;
import com.example.moviemanager.service.tmdbmovieservice.model.MovieDetails;
import com.example.moviemanager.service.tmdbmovieservice.model.MovieDetailsResponse;
import com.example.moviemanager.service.tmdbmovieservice.model.MovieType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class TmdbMovieService {

    private static final String PATH = "/3/search/";
    private static final String QUERY = "query";
    private static final String API_KEY = "api_key";
    private static final String MOVIE_TYPE = "movie";
    private static final String TV_SHOW_TYPE = "tv";
    private static final String MOVIE_RELEASE_YEAR = "year";
    private static final String TV_SHOW_RELEASE_YEAR = "first_air_date_year";

    private final ConfigProperties configProperties;
    private final RestTemplate restTemplate;

    public TmdbMovieService(ConfigProperties configProperties, RestTemplate restTemplate) {
        this.configProperties = configProperties;
        this.restTemplate = restTemplate;
    }

    public Movie getMovie(FindMovieInfo findMovieInfo) {
        String endpoint = buildEndpoint(findMovieInfo);
        MovieDetailsResponse movieDetailsResponse = getResponse(endpoint);
        MovieDetails movieDetails = retrieveMovieDetails(movieDetailsResponse);

        return buildMovie(findMovieInfo, movieDetails);
    }

    private String buildEndpoint(FindMovieInfo findMovieInfo) {
        MovieType movieType = findMovieInfo.type();
        String pathComponent = movieType.equals(MovieType.MOVIE) ? MOVIE_TYPE : TV_SHOW_TYPE;
        String releaseYear = movieType.equals(MovieType.MOVIE) ? MOVIE_RELEASE_YEAR : TV_SHOW_RELEASE_YEAR;
        UriComponentsBuilder uri = UriComponentsBuilder
                .newInstance()
                .path(PATH + pathComponent)
                .queryParam(API_KEY, configProperties.getApiKey())
                .queryParam(QUERY, findMovieInfo.title())
                .queryParam(releaseYear, findMovieInfo.releaseYear());

        return uri.build().toUriString();
    }

    private MovieDetailsResponse getResponse(String endpoint) {
        Optional<MovieDetailsResponse> movieDetailsResponse =
                Optional.ofNullable(restTemplate.getForObject(endpoint, MovieDetailsResponse.class));
        return movieDetailsResponse.orElseThrow();
    }

    private MovieDetails retrieveMovieDetails(MovieDetailsResponse movieDetailsResponse) {
        if (movieDetailsResponse.results() == null || movieDetailsResponse.results().size() != 1) {
            throw new UniqueMovieDetailsNotFoundException("Can`t find unique movie according to provided details.");
        }
        return movieDetailsResponse.results().get(0);
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
