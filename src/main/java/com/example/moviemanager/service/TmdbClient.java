package com.example.moviemanager.service;

import com.example.moviemanager.configuration.TmdbConfigProperties;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import com.example.moviemanager.service.model.MovieType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbClient {

    private static final String PATH = "/3/search/";
    private static final String QUERY = "query";
    private static final String API_KEY = "api_key";
    private static final String MOVIE_TYPE = "movie";
    private static final String TV_SHOW_TYPE = "tv";
    private static final String MOVIE_RELEASE_YEAR = "year";
    private static final String TV_SHOW_RELEASE_YEAR = "first_air_date_year";

    private final TmdbConfigProperties tmdbConfigProperties;
    private final RestTemplate restTemplate;

    public TmdbClient(TmdbConfigProperties tmdbConfigProperties, RestTemplate restTemplate) {
        this.tmdbConfigProperties = tmdbConfigProperties;
        this.restTemplate = restTemplate;
    }

    public MovieDetailsResponse findMovies(FindMovieInfo findMovieInfo) {
        String endpoint = buildUri(findMovieInfo);
        return restTemplate.getForObject(endpoint, MovieDetailsResponse.class);
    }

    private String buildUri(FindMovieInfo findMovieInfo) {
        MovieType movieType = findMovieInfo.type();
        String pathComponent = movieType.equals(MovieType.MOVIE) ? MOVIE_TYPE : TV_SHOW_TYPE;
        String releaseYear = movieType.equals(MovieType.MOVIE) ? MOVIE_RELEASE_YEAR : TV_SHOW_RELEASE_YEAR;

        return UriComponentsBuilder
                .newInstance()
                .path(PATH + pathComponent)
                .queryParam(API_KEY, tmdbConfigProperties.apiKey())
                .queryParam(QUERY, findMovieInfo.title())
                .queryParam(releaseYear, findMovieInfo.releaseYear())
                .build()
                .toUriString();
    }
}
