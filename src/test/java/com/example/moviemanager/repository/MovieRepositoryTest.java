package com.example.moviemanager.repository;

import com.example.moviemanager.repository.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class MovieRepositoryTest {

    private static final UUID ID_1 = UUID.fromString("926e09f7-3c78-469d-bdbc-2d34d314c1b4");
    private static final UUID ID_2 = UUID.fromString("2d88924f-0f63-4280-9e58-a9a126049273");

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void return_collection_of_movies() {
        // given
        movieRepository.saveAll(List.of(
                new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(ID_2, "Home Alone 2", "Christmas movie 2", 1992, 8.8)
        ));

        // when
        List<Movie> actualMovies = movieRepository.findAll();

        // then
        assertThat(actualMovies)
                .extracting("id")
                .containsExactly(ID_1, ID_2);
    }

    @Test
    void returns_movie_by_id() {
        // given
        movieRepository.saveAll(List.of(
                new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(ID_2, "Home Alone 2", "Christmas movie 2", 1992, 8.8)
        ));

        // when
        Optional<Movie> actualMovie = movieRepository.findById(ID_1);

        // then
        assertThat(ID_1)
                .isEqualTo(actualMovie.get().getId());
    }

    @Test
    void saves_movie() {
        // when
        Movie savedMovie = movieRepository.save(
                new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5)
        );

        // then
        assertThat(ID_1)
                .isEqualTo(savedMovie.getId());
    }

    @Test
    void deletes_movie_by_id() {
        // given
        movieRepository.saveAll(List.of(
                new Movie(ID_1, "Home Alone", "Christmas movie", 1990, 8.5),
                new Movie(ID_2, "Home Alone 2", "Christmas movie 2", 1992, 8.8)
        ));

        // when
        movieRepository.deleteById(ID_1);
        List<Movie> actualMovies = movieRepository.findAll();

        // then
        assertThat(actualMovies)
                .hasSize(1)
                .extracting("id")
                .containsOnly(ID_2);
    }
}
