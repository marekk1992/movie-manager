package com.example.moviemanager.controller;

import com.example.moviemanager.controller.dto.MovieRequest;
import com.example.moviemanager.controller.dto.MovieResponse;
import com.example.moviemanager.controller.dto.MoviesResponse;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.MovieService;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.containsString;
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

    public static final String MOVIES_URL = "/v1/movies";
    public static final String MOVIES_URL_WITH_ID = MOVIES_URL + "/{movieId}";

    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returns_list_of_all_movies() throws Exception {
        // given
        List<Movie> movies = List.of(
                new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(2, "Home Alone 2", "Christmas movie", 1992, 8.9));
        when(movieService.findAll()).thenReturn(movies);
        String responseBody = objectMapper.writeValueAsString(MoviesResponse.fromEntity(movies));

        // then
        mockMvc.perform(get(MOVIES_URL))
                .andExpect(status().isOk())
                .andExpect(content().string(responseBody));
    }

    @Test
    void returns_movie_by_id() throws Exception {
        // given
        Integer movieId = 1;
        Movie expectedMovie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieService.findById(movieId)).thenReturn(expectedMovie);
        String responseBody = objectMapper.writeValueAsString(MovieResponse.fromEntity(expectedMovie));

        // then
        mockMvc.perform(get(MOVIES_URL_WITH_ID, movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(responseBody));
    }

    @Test
    void returns_response_404_when_trying_to_get_non_existing_movie() throws Exception {
        // given
        Integer movieId = 1;
        String message = "Could not find Movie by id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).findById(movieId);

        // then
        mockMvc.perform(get(MOVIES_URL_WITH_ID, movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void saves_movie() throws Exception {
        // given
        MovieRequest request = new MovieRequest("Home Alone", "Christmas movie", 1990, 8.5);
        String requestBody = objectMapper.writeValueAsString(request);
        Movie givenMovie = request.toEntity();

        Movie expectedmovie = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        String responseBody = objectMapper.writeValueAsString(MovieResponse.fromEntity(expectedmovie));
        when(movieService.save(givenMovie)).thenReturn(expectedmovie);

        // then
        mockMvc.perform(post(MOVIES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string(responseBody));
    }

    @Test
    void deletes_movie_by_id() throws Exception {
        // given
        Integer movieId = 1;
        doNothing().when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete(MOVIES_URL_WITH_ID, movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(blankString()));
    }

    @Test
    void returns_404_response_when_trying_to_delete_non_existing_movie() throws Exception {
        // given
        Integer movieId = 1;
        String message = "Deletion failed. Could not find movie with id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete(MOVIES_URL_WITH_ID, movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void updates_movie_by_id_with_provided_data() throws Exception {
        // given
        Integer movieId = 1;
        MovieRequest request = new MovieRequest("Home Alone", "Christmas movie", 1990, 8.5);
        Movie givenMovie = request.toEntity();
        String requestBody = objectMapper.writeValueAsString(request);

        Movie expectedMovie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);
        String responseBody = objectMapper.writeValueAsString(MovieResponse.fromEntity(expectedMovie));
        when(movieService.update(movieId, givenMovie)).thenReturn(expectedMovie);

        // then
        mockMvc.perform(put(MOVIES_URL_WITH_ID, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(responseBody));
    }

    @Test
    void returns_404_response_when_trying_to_update_non_existing_movie() throws Exception {
        // given
        Integer movieId = 1;
        MovieRequest request = new MovieRequest("Home Alone", "Christmas movie", 1990, 8.5);
        String requestBody = objectMapper.writeValueAsString(request);
        Movie givenMovie = request.toEntity();
        String message = "Update failed. Could not find movie by id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).update(movieId, givenMovie);

        // then
        mockMvc.perform(put(MOVIES_URL_WITH_ID, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void returns_500_response_when_trying_to_delete_movie_without_specifying_its_id() throws Exception {
        // given
        String message = "Request method 'DELETE' is not supported";

        // then
        mockMvc.perform(delete(MOVIES_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(message)));
    }
}
