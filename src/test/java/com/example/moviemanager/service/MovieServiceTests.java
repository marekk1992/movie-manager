package com.example.moviemanager.service;

import com.example.moviemanager.entity.Movie;
import com.example.moviemanager.repo.MovieRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MovieServiceTests {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    public void returns_list_of_all_movies() {
        // given
        Movie movie1 = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        Movie movie2 = new Movie(2, "Home Alone 2", "Christmas movie", 1992, 8.9);

        List<Movie> expectedMovies = new ArrayList<>();
        expectedMovies.add(movie1);
        expectedMovies.add(movie2);

        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // when
        List<Movie> actualMovies = movieService.findAll();

        // then
        assertEquals(expectedMovies, actualMovies);
    }

    @Test
    public void throws_exception_when_trying_to_find_non_existing_movie() { // TODO review
        // given
        int id = 2;
        String expectedMessage = "Could not find Movie by id - " + id;
        when(movieRepository.findById(2)).thenThrow(new MovieNotFoundException(("Could not find Movie by id - " + id)));

        // when
        MovieNotFoundException thrown =
                assertThrows(MovieNotFoundException.class, () -> movieService.findById(2));
        String actualMessage = thrown.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void returns_exactly_one_movie() {
        // given
        Movie expectedMovie = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieRepository.findById(1)).thenReturn(Optional.of(expectedMovie));

        // when
        Movie actualMovie = movieService.findById(1);

        // then
        assertEquals(expectedMovie, actualMovie);
    }

    @Test
    public void throws_exception_when_trying_to_delete_non_existing_movie() {
        // given
        int id = 2;
        String expectedMessage = "Could not find Movie by id - " + id;
        doThrow(new MovieNotFoundException(expectedMessage)).when(movieRepository).deleteById(2);

        // when
        MovieNotFoundException thrown =
                assertThrows(MovieNotFoundException.class, () -> movieService.deleteById(2));
        String actualMessage = thrown.getMessage();

        // then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deletes_one_movie() {
        // given
        int movieId = 1;
        doNothing().when(movieRepository).deleteById(movieId);

        // when
        movieService.deleteById(1);

        // then
        verify(movieRepository, times(1)).deleteById(movieId);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    public void saves_one_movie() {
        // given
        Movie expected = new Movie(1, "Home Alone", "Christmas movie", 1990, 8.5);
        when(movieRepository.save(expected)).thenReturn(expected);

        // when
        Movie actual = movieService.save(expected);

        // then
        assertEquals(expected, actual);
    }
}
