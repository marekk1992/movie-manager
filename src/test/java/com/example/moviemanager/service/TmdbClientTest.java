package com.example.moviemanager.service;

import com.example.moviemanager.configuration.TmdbConfigProperties;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import com.example.moviemanager.service.model.MovieType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

public class TmdbClientTest {

    private static final WireMockServer wireMockServer = new WireMockServer(0);

    private final TmdbConfigProperties tmdbConfigProperties =
            new TmdbConfigProperties("http://localhost:" + wireMockServer.port(), "43d5d872d7dc32845ab997b8v25987f7");

    private final RestTemplate restTemplate = new RestTemplateBuilder().rootUri(tmdbConfigProperties.baseUrl()).build();

    private final TmdbClient tmdbClient = new TmdbClient(tmdbConfigProperties, restTemplate);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void startServer() {
        wireMockServer.start();
    }

    @AfterAll
    public static void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void returns_tv_show_response_according_to_given_title_and_release_year() throws JsonProcessingException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo("GAME OF THRONES", MovieType.TV_SHOW, 2011);
        MovieDetailsResponse expectedMovieDetailsResponse =
                new MovieDetailsResponse(List.of(new MovieDetails("GOT Tv-show.", 8.4)));
        String tvShowsUrl = "/3/search/tv?api_key=43d5d872d7dc32845ab997b8v25987f7" +
                              "&query=GAME%20OF%20THRONES&first_air_date_year=2011";
        wireMockServer.stubFor(get(tvShowsUrl)
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(expectedMovieDetailsResponse))
                )
        );

        // when
        MovieDetailsResponse actualMovieDetailsResponse = tmdbClient.findMovies(findMovieInfo);

        // then
        assertThat(actualMovieDetailsResponse).isEqualTo(expectedMovieDetailsResponse);
    }

    @Test
    public void returns_movie_response_according_to_given_title_and_release_year() throws JsonProcessingException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo("HOME ALONE", MovieType.MOVIE, 1990);
        MovieDetailsResponse expectedMovieDetailsResponse =
                new MovieDetailsResponse(List.of(new MovieDetails("Christmas movie", 7.4)));
        String moviesUrl = "/3/search/movie?api_key=43d5d872d7dc32845ab997b8v25987f7" +
                            "&query=HOME%20ALONE&year=1990";
        wireMockServer.stubFor(get(moviesUrl)
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(expectedMovieDetailsResponse))
                )
        );

        // when
        MovieDetailsResponse actualMovieDetailsResponse = tmdbClient.findMovies(findMovieInfo);

        // then
        assertThat(actualMovieDetailsResponse).isEqualTo(expectedMovieDetailsResponse);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_find_movie_parameters.csv", numLinesToSkip = 1)
    public void returns_empty_movie_response_when_provided_non_existing_movie_parameters(
            String title, MovieType type, int releaseYear, String url
    ) throws JsonProcessingException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo(title, type, releaseYear);
        MovieDetailsResponse expectedMovieDetailsResponse = new MovieDetailsResponse(Collections.emptyList());
        wireMockServer.stubFor(get(url)
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(expectedMovieDetailsResponse))
                )
        );

        // when
        MovieDetailsResponse actualMovieDetailsResponse = tmdbClient.findMovies(findMovieInfo);

        // then
        assertThat(actualMovieDetailsResponse).isEqualTo(expectedMovieDetailsResponse);
    }
}
