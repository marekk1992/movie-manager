package com.example.moviemanager.service;

import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void returns_list_of_all_movies() {
        // given
        List<Movie> expectedMovies = List.of(
                new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(2, "Home Alone 2", "Christmas movie", 1992, 8.9));
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // when
        List<Movie> actualMovies = movieService.findAll();

        // then
        assertEquals(expectedMovies, actualMovies);
    }

    @Test
    void throws_exception_when_trying_to_find_non_existing_movie() {
        // given
        int movieId = 2;
        String expectedMessage = "Could not find movie by id - " + movieId;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.findById(movieId))
                .withMessage(expectedMessage);
    }

    @Test
    void finds_movie_by_id() {
        // given
        int movieId = 1;
        Movie expectedMovie = new Movie(movieId, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(expectedMovie));

        // when
        Movie actualMovie = movieService.findById(movieId);

        // then
        assertEquals(expectedMovie, actualMovie);
    }

    @Test
    void throws_exception_when_trying_to_delete_non_existing_movie() {
        // given
        int movieId = 2;
        String expectedMessage = "Deletion failed. Could not find movie with id - " + movieId;
        doThrow(EmptyResultDataAccessException.class).when(movieRepository).deleteById(movieId);

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.deleteById(movieId))
                .withMessage(expectedMessage);
    }

    @Test
    void deletes_movie_by_id() {
        // given
        int movieId = 1;
        doNothing().when(movieRepository).deleteById(movieId);

        // when
        movieService.deleteById(movieId);

        // then
        verify(movieRepository, times(1)).deleteById(movieId);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void saves_movie() {
        // given
        Movie expected = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieRepository.save(expected)).thenReturn(expected);

        // when
        Movie actual = movieService.save(expected);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void updates_movie_by_id_with_provided_data() {
        // given
        int movieId = 1;
        when(movieRepository.findById(movieId))
                .thenReturn(Optional.of(new Movie(movieId, "Home Alone 2", "Comedy", 1992, 9.0)));

        Movie expected = new Movie(5, "Home Alone", "Christmas movie", 1990, 8.5);
        expected.setId(movieId);
        when(movieRepository.save(expected)).thenReturn(expected);

        // when
        Movie actual = movieService.update(movieId, expected);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void throws_exception_when_trying_to_update_non_existing_movie() {
        // given
        int movieId = 5;
        Movie movie = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        String expectedMessage = "Update failed. Could not find movie by id - " + movieId;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.update(movieId, movie))
                .withMessage(expectedMessage);
    }
}
