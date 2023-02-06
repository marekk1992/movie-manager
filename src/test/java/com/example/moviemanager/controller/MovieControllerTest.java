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
import java.util.UUID;

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

    private static final UUID ID_1 = UUID.fromString("926e09f7-3c78-469d-bdbc-2d34d314c1b4");
    private static final UUID ID_2 = UUID.fromString("2d88924f-0f63-4280-9e58-a9a126049273");
    private static final String MOVIES_URL = "/v1/movies";
    private static final String MOVIE_BY_ID_URL = MOVIES_URL + "/{movieId}";

    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returns_collection_of_movies() throws Exception {
        // given
        when(movieService.findAll()).thenReturn(List.of(
                new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(ID_2, "Home Alone 2", "Christmas movie", 1992, 8.9)
        ));

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
                                      "id":926e09f7-3c78-469d-bdbc-2d34d314c1b4,
                                      "title":"Home Alone",
                                      "description":"Christmas movie",
                                      "releaseYear":1990,
                                      "rating":8.5
                                  },
                                  {
                                      "id":2d88924f-0f63-4280-9e58-a9a126049273,
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
        when(movieService.findById(ID_1))
                .thenReturn(new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5));

        // when
        String actualResponseBody = mockMvc.perform(get(MOVIE_BY_ID_URL, ID_1))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                               "id":926e09f7-3c78-469d-bdbc-2d34d314c1b4,
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
        String message = "Could not find Movie by id - " + ID_1;
        doThrow(new MovieNotFoundException(message)).when(movieService).findById(ID_1);

        // then
        mockMvc.perform(get(MOVIE_BY_ID_URL, ID_1))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void creates_movie() throws Exception {
        // given
        CreateMovieRequest createMovieRequest = new CreateMovieRequest(
                "Home Alone", "Christmas movie", 1990, 8.5
        );
        Movie expectedmovie = new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5);
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
                               "id":926e09f7-3c78-469d-bdbc-2d34d314c1b4,
                               "title":"Home Alone",
                               "description":"Christmas movie",
                               "releaseYear":1990,
                               "rating":8.5
                           }
                        """,
                actualResponseBody, true);
    }

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

    @Test
    void updates_movie_by_id() throws Exception {
        // given
        UpdateMovieRequest updateMovieRequest = new UpdateMovieRequest(
                "Home Alone", "Christmas movie", 1990, 8.5
        );
        Movie expectedMovie = new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieService.update(eq(ID_1), argThat(matchUpdateMovieRequestToEntity(updateMovieRequest))))
                .thenReturn(expectedMovie);

        // when
        String actualResponseBody = mockMvc.perform(put(MOVIE_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMovieRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        JSONAssert.assertEquals(
                """
                           {
                               "id":926e09f7-3c78-469d-bdbc-2d34d314c1b4,
                               "title":"Home Alone",
                               "description":"Christmas movie",
                               "releaseYear":1990,
                               "rating":8.5
                           }
                        """,
                actualResponseBody, true);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/invalid_movie_request_parameters.csv", numLinesToSkip = 1)
    void returns_505_response_when_user_input_validation_for_updating_movie_is_failed(
            String title, String description, int releaseYear, double rating, String message
    ) throws Exception {
        // given
        String requestBody = objectMapper
                .writeValueAsString(new UpdateMovieRequest(title, description, releaseYear, rating));

        // then
        mockMvc.perform(put(MOVIE_BY_ID_URL, ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_404_response_when_trying_to_update_non_existing_movie() throws Exception {
        // given
        String message = "Update failed. Could not find movie by id - " + ID_1;
        doThrow(new MovieNotFoundException(message)).when(movieService).update(any(UUID.class), any(Movie.class));

        // then
        mockMvc.perform(put(MOVIE_BY_ID_URL, ID_1)
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
    void deletes_movie_by_id() throws Exception {
        // given
        doNothing().when(movieService).deleteById(ID_1);

        // then
        mockMvc.perform(delete(MOVIE_BY_ID_URL, ID_1))
                .andExpect(status().isOk())
                .andExpect(content().string(blankString()));
    }

    @Test
    void returns_404_response_when_trying_to_delete_non_existing_movie() throws Exception {
        // given
        String message = "Deletion failed. Could not find movie with id - " + ID_1;
        doThrow(new MovieNotFoundException(message)).when(movieService).deleteById(ID_1);

        // then
        mockMvc.perform(delete(MOVIE_BY_ID_URL, ID_1))
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
