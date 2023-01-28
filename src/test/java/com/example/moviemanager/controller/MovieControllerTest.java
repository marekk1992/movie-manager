package com.example.moviemanager.controller;

import com.example.moviemanager.controller.dto.CreateMovieRequest;
import com.example.moviemanager.controller.dto.UpdateMovieRequest;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.MovieService;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentMatcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class MovieControllerTest {

    private static final String MOVIES_URL = "/v1/movies";
    private static final String MOVIE_BY_ID_URL = MOVIES_URL + "/{movieId}";

    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_movie_request_parameters.csv", numLinesToSkip = 1)
    void returns_505_response_when_user_input_validation_for_creating_movie_is_failed(
            String title, String description, int releaseYear, double rating, String message
    ) throws Exception {
        // given
        String requestBody = objectMapper
                .writeValueAsString(new CreateMovieRequest(title, description, releaseYear, rating));

        // then
        mockMvc.perform(post(MOVIES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_movie_request_parameters.csv", numLinesToSkip = 1)
    void returns_505_response_when_user_input_validation_for_updating_movie_is_failed(
            String title, String description, int releaseYear, double rating, String message
    ) throws Exception {
        // given
        String requestBody = objectMapper
                .writeValueAsString(new CreateMovieRequest(title, description, releaseYear, rating));

        // then
        mockMvc.perform(put(MOVIE_BY_ID_URL, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_list_of_all_movies() throws Exception {
        // given
        when(movieService.findAll()).thenReturn(List.of(
                new Movie(1L, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(2L, "Home Alone 2", "Christmas movie", 1992, 8.9)));

        // when
        String actualResponseBody = mockMvc.perform(get(MOVIES_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                              "movies": [
                                  {
                                      "id":1,
                                      "title":"Home Alone",
                                      "description":"Christmas movie",
                                      "releaseYear":1990,
                                      "rating":8.5
                                  },
                                  {
                                      "id":2,
                                      "title":"Home Alone 2",
                                      "description":"Christmas movie",
                                      "releaseYear":1992,
                                      "rating":8.9
                                  }
                              ]
                           }
                        """,
                actualResponseBody, true);
    }

    @Test
    void returns_movie_by_id() throws Exception {
        // given
        long movieId = 1;
        when(movieService.findById(movieId))
                .thenReturn(new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5));

        // when
        String actualResponseBody = mockMvc.perform(get(MOVIE_BY_ID_URL, movieId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                               "id":1,
                               "title":"Home Alone",
                               "description":"Christmas movie",
                               "releaseYear":1990,
                               "rating":8.5
                           }
                        """,
                actualResponseBody, true);
    }

    @Test
    void returns_response_404_when_trying_to_get_non_existing_movie() throws Exception {
        // given
        long movieId = 1;
        String message = "Could not find Movie by id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).findById(movieId);

        // then
        mockMvc.perform(get(MOVIE_BY_ID_URL, movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void creates_movie() throws Exception {
        // given
        CreateMovieRequest createMovieRequest =
                new CreateMovieRequest("Home Alone", "Christmas movie", 1990, 8.5);
        Movie expectedmovie = new Movie(1L, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieService.save(argThat(matchCreateMovieRequestToEntity(createMovieRequest)))).thenReturn(expectedmovie);

        // when
        String actualResponseBody = mockMvc.perform(post(MOVIES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMovieRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                               "id":1,
                               "title":"Home Alone",
                               "description":"Christmas movie",
                               "releaseYear":1990,
                               "rating":8.5
                           }
                        """,
                actualResponseBody, true);
    }

    @Test
    void deletes_movie_by_id() throws Exception {
        // given
        long movieId = 1;
        doNothing().when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete(MOVIE_BY_ID_URL, movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(blankString()));
    }

    @Test
    void returns_404_response_when_trying_to_delete_non_existing_movie() throws Exception {
        // given
        long movieId = 1;
        String message = "Deletion failed. Could not find movie with id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete(MOVIE_BY_ID_URL, movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void updates_movie_by_id() throws Exception {
        // given
        long movieId = 1;
        UpdateMovieRequest updateMovieRequest =
                new UpdateMovieRequest("Home Alone", "Christmas movie", 1990, 8.5);
        Movie expectedMovie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieService.update(eq(1L), argThat(matchUpdateMovieRequestToEntity(updateMovieRequest))))
                .thenReturn(expectedMovie);

        // when
        String actualResponseBody = mockMvc.perform(put(MOVIE_BY_ID_URL, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMovieRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                               "id":1,
                               "title":"Home Alone",
                               "description":"Christmas movie",
                               "releaseYear":1990,
                               "rating":8.5
                           }
                        """,
                actualResponseBody, true);
    }

    @Test
    void returns_404_response_when_trying_to_update_non_existing_movie() throws Exception {
        // given
        long movieId = 1;
        String message = "Update failed. Could not find movie by id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).update(any(Long.class), any(Movie.class));

        // then
        mockMvc.perform(put(MOVIE_BY_ID_URL, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateMovieRequest("Home Alone",
                                        "Christmas movie",
                                        1990,
                                        8.5
                                )
                        )))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_trying_to_delete_movie_without_specifying_its_id() throws Exception {
        // expect
        mockMvc.perform(delete(MOVIES_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Request method 'DELETE' is not supported")));
    }

    private ArgumentMatcher<Movie> matchCreateMovieRequestToEntity(CreateMovieRequest createMovieRequest) {
        return movie -> movie.getTitle().equals(createMovieRequest.title()) &&
                        movie.getDescription().equals(createMovieRequest.description()) &&
                        movie.getReleaseYear() == createMovieRequest.releaseYear() &&
                        movie.getRating() == createMovieRequest.rating();
    }

    private ArgumentMatcher<Movie> matchUpdateMovieRequestToEntity(UpdateMovieRequest updateMovieRequest) {
        return movie -> movie.getTitle().equals(updateMovieRequest.title()) &&
                        movie.getDescription().equals(updateMovieRequest.description()) &&
                        movie.getReleaseYear() == updateMovieRequest.releaseYear() &&
                        movie.getRating() == updateMovieRequest.rating();
    }
}
