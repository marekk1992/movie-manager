package com.example.moviemanager.repository;

import com.example.moviemanager.repository.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void return_collection_of_movies() {
        // given
        movieRepository.save(new Movie("Home Alone", "Christmas movie", 1990, 8.5));
        movieRepository.save(new Movie("Home Alone 2", "Christmas movie 2", 1992, 8.8));

        // when
        List<Movie> actualMovies = movieRepository.findAll();
        List<Long> moviesId = List.of(actualMovies.get(0).getId(), actualMovies.get(1).getId());

        // then
        assertTrue(moviesId.contains(1L) && moviesId.contains(2L));
    }

    @Test
    void returns_movie_by_id() {
        // given
        movieRepository.save(new Movie("Home Alone", "Christmas movie", 1990, 8.5));
        movieRepository.save(new Movie("Home Alone 2", "Christmas movie 2", 1992, 8.8));

        // when
        Optional<Movie> actualMovie = movieRepository.findById(1L);

        // then
        assertEquals(1L, actualMovie.get().getId());
    }

    @Test
    void returns_empty_value_if_movie_not_exists() {
        // when
        Optional<Movie> actualMovie = movieRepository.findById(1L);

        // then
        assertTrue(actualMovie.isEmpty());
    }

    @Test
    void saves_movie() {
        // when
        Movie savedMovie = movieRepository.save(
                new Movie("Home Alone", "Christmas movie", 1990, 8.5)
        );

        // then
        assertEquals(1L, savedMovie.getId());
    }

    @Test
    void deletes_movie_by_id() {
        // given
        movieRepository.save(new Movie("Home Alone", "Christmas movie", 1990, 8.5));
        movieRepository.save(new Movie("Home Alone 2", "Christmas movie 2", 1992, 8.8));

        // when
        movieRepository.deleteById(1L);
        Optional<Movie> remainingMovie = movieRepository.findById(2L);
        List<Movie> actualMovies = movieRepository.findAll();

        // then
        assertTrue(actualMovies.size() == 1 && remainingMovie.get().getId() == 2L);
    }
}
