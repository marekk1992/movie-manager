package com.example.moviemanager.controller;

import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.exception.MovieNotFoundException;
import com.example.moviemanager.service.MovieService;
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

        // then
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(movies)));
    }

    @Test
    void returns_movie_by_given_id() throws Exception {
        // given
        int movieId = 1;
        Movie movie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);

        when(movieService.findById(movieId)).thenReturn(movie);

        // then
        mockMvc.perform(get("/movies/{movieId}", movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(movie)));
    }

    @Test
    void throws_exception_when_trying_to_get_non_existing_movie() throws Exception {
        // given
        int movieId = 1;
        String message = "Could not find Movie by id - " + movieId;

        when(movieService.findById(movieId)).thenThrow(new MovieNotFoundException(message));

        // then
        mockMvc.perform(get("/movies/{movieId}", movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void saves_movie() throws Exception {
        // given
        Movie movie = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        String jsonMovie = objectMapper.writeValueAsString(movie);
        when(movieService.save(movie)).thenReturn(movie);

        // then
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMovie))
                .andExpect(status().isCreated())
                .andExpect(content().string(jsonMovie));
    }

    @Test
    void deletes_movie_by_given_id() throws Exception {
        // given
        int movieId = 1;
        doNothing().when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete("/movies/{movieId}", movieId))
                .andExpect(status().isOk())
                .andExpect(content().string(blankString()));
    }

    @Test
    void throws_exception_when_trying_to_delete_non_existing_movie() throws Exception {
        // given
        int movieId = 1;
        String message = "Deletion failed. Could not find movie with id - " + movieId;

        doThrow(new MovieNotFoundException(message)).when(movieService).deleteById(movieId);

        // then
        mockMvc.perform(delete("/movies/{movieId}", movieId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void updates_movie() throws Exception {
        // given
        int movieId = 1;
        Movie movie = new Movie(5, "Home Alone", "Christmas movie", 1990, 8.5);
        Movie expectedMovie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);
        String jsonMovie = objectMapper.writeValueAsString(movie);
        String expectedJsonMovie = objectMapper.writeValueAsString(expectedMovie);
        when(movieService.update(movieId, movie)).thenReturn(expectedMovie);

        // then
        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMovie))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJsonMovie));
    }

    @Test
    void throws_exception_when_trying_to_update_non_existing_movie() throws Exception {
        // given
        int movieId = 1;
        Movie movie = new Movie(5, "Home Alone", "Christmas movie", 1990, 8.5);
        String jsonMovie = objectMapper.writeValueAsString(movie);
        String message = "Update failed. Could not find movie by id - " + movieId;
        doThrow(new MovieNotFoundException(message)).when(movieService).update(movieId, movie);

        // then
        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMovie))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(message)));
    }
}
