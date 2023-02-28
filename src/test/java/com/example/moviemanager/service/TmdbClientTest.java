package com.example.moviemanager.service;

import com.example.moviemanager.configuration.TmdbConfigProperties;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import com.example.moviemanager.service.model.MovieType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import wiremock.org.apache.hc.core5.http.HttpHeaders;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

public class TmdbClientTest {

    private static final String API_KEY = "43d5d872d7dc32845ab997b8v25987f7";
    private static final String TV_SHOWS_URL = "/3/search/tv?api_key=" + API_KEY;
    private static final String MOVIES_URL = "/3/search/movie?api_key=" + API_KEY;

    private static final WireMockServer wireMockServer = new WireMockServer(0);

    private final TmdbConfigProperties tmdbConfigProperties =
            new TmdbConfigProperties("http://localhost:" + wireMockServer.port(), API_KEY);

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
    public void returns_tv_show_response_according_to_given_title_and_release_year() throws JsonProcessingException, JSONException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo("Game of Thrones", MovieType.TV_SHOW, 2011);
        wireMockServer.stubFor(get(TV_SHOWS_URL + buildQuery(findMovieInfo))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(
                                new MovieDetailsResponse(List.of(new MovieDetails("GOT Tv-show.", 8.4)))
                        ))
                )
        );

        // when
        String actualMovieDetailsResponse = objectMapper.writeValueAsString(tmdbClient.find(findMovieInfo));
        System.out.println(actualMovieDetailsResponse);

        // then
        JSONAssert.assertEquals(
                """
                          {   "results": [
                                   {
                                       "overview": "GOT Tv-show.",
                                       "vote_average": 8.4
                                   }
                              ]
                          }
                        """, actualMovieDetailsResponse, true);
    }

    @Test
    public void returns_movie_response_according_to_given_title_and_release_year() throws JsonProcessingException, JSONException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo("Home Alone", MovieType.MOVIE, 1990);
        wireMockServer.stubFor(get(MOVIES_URL + buildQuery(findMovieInfo))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(
                                new MovieDetailsResponse(List.of(new MovieDetails("Christmas movie.", 7.4)))
                        ))
                )
        );

        // when
        String actualMovieDetailsResponse = objectMapper.writeValueAsString(tmdbClient.find(findMovieInfo));

        // then
        JSONAssert.assertEquals(
                """
                          {   "results": [
                                   {
                                       "overview": "Christmas movie.",
                                       "vote_average": 7.4
                                   }
                              ]
                          }
                        """, actualMovieDetailsResponse, true);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_find_movie_parameters.csv", numLinesToSkip = 1)
    public void returns_empty_response_when_provided_non_existing_movie_parameters(
            String title, MovieType type, int releaseYear
    ) throws JsonProcessingException, JSONException {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo(title, type, releaseYear);
        String url = type.equals(MovieType.MOVIE) ? MOVIES_URL : TV_SHOWS_URL;
        wireMockServer.stubFor(get(url + buildQuery(findMovieInfo))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.OK.value())
                        .withBody(objectMapper.writeValueAsString(
                                new MovieDetailsResponse(Collections.emptyList())
                        ))
                )
        );

        // when
        String actualMovieDetailsResponse = objectMapper.writeValueAsString(tmdbClient.find(findMovieInfo));

        // then
        JSONAssert.assertEquals(
                """
                          {   "results": [
                              ]
                          }
                        """, actualMovieDetailsResponse, true);
    }

    private String buildQuery(FindMovieInfo findMovieInfo) {
        String releaseYearQuery = findMovieInfo.type().equals(MovieType.MOVIE) ? "&year=" : "&first_air_date_year=";
        return "&query=" +
               findMovieInfo.title().replaceAll(" ", "%20") +
               releaseYearQuery +
               findMovieInfo.releaseYear();
    }
}
