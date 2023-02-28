package com.example.moviemanager.service;

import com.example.moviemanager.repository.MovieRepository;
import com.example.moviemanager.repository.model.Movie;
import com.example.moviemanager.service.exception.MovieNotFoundException;
import com.example.moviemanager.service.model.FindMovieInfo;
import com.example.moviemanager.service.model.MovieDetails;
import com.example.moviemanager.service.model.MovieDetailsResponse;
import com.example.moviemanager.service.model.MovieType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    private static final UUID ID_1 = UUID.fromString("926e09f7-3c78-469d-bdbc-2d34d314c1b4");
    private static final UUID ID_2 = UUID.fromString("2d88924f-0f63-4280-9e58-a9a126049273");

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TmdbClient tmdbClient;

    @InjectMocks
    private MovieService movieService;

    @Test
    void returns_collection_of_movies() {
        // given
        List<Movie> expectedMovies = List.of(
                new Movie(ID_1, "HOME ALONE", "Christmas movie", 1990, 8.5),
                new Movie(ID_2, "HOME ALONE 2", "Christmas movie", 1992, 8.9)
        );
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // when
        List<Movie> actualMovies = movieService.findAll();

        // then
        assertThat(actualMovies)
                .isEqualTo(expectedMovies);
    }

    @Test
    void finds_movie_by_id() {
        // given
        Movie expectedMovie = new Movie(ID_1, "HOME ALONE", "Christmas movie", 1990, 8.5);
        when(movieRepository.findById(ID_1)).thenReturn(Optional.of(expectedMovie));

        // when
        Movie actualMovie = movieService.findById(ID_1);

        // then
        assertThat(actualMovie)
                .isEqualTo(expectedMovie);
    }

    @Test
    void throws_exception_when_trying_to_find_non_existing_movie() {
        // given
        when(movieRepository.findById(ID_1)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.findById(ID_1))
                .withMessage("Could not find movie by id - " + ID_1);
    }

    @Test
    void deletes_movie_by_id() {
        // given
        doNothing().when(movieRepository).deleteById(ID_1);

        // when
        movieService.deleteById(ID_1);

        // then
        verify(movieRepository, times(1)).deleteById(ID_1);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void throws_exception_when_trying_to_delete_non_existing_movie() {
        // given
        doThrow(EmptyResultDataAccessException.class).when(movieRepository).deleteById(ID_1);

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.deleteById(ID_1))
                .withMessage("Deletion failed. Could not find movie with id - " + ID_1);
    }

    @Test
    void saves_movie() {
        // given
        FindMovieInfo findMovieInfo = new FindMovieInfo("HOME ALONE", MovieType.MOVIE, 1990);
        MovieDetailsResponse movieDetailsResponse = new MovieDetailsResponse(
                List.of(new MovieDetails("Christmas Movie", 8.5))
        );
        Movie expectedMovie = new Movie(ID_1, "HOME ALONE", "Christmas movie", 1990, 8.5);
        when(tmdbClient.find(findMovieInfo)).thenReturn(movieDetailsResponse);
        when(movieRepository.save(argThat(matchesMovieInfoAndDetailsToEntity(findMovieInfo, movieDetailsResponse.results().get(0)))))
                .thenReturn(expectedMovie);

        // when
        Movie actualMovie = movieService.save(findMovieInfo);

        // then
        assertThat(actualMovie)
                .isEqualTo(expectedMovie);
    }

    @Test
    void updates_movie_by_id_with_provided_data() {
        // given
        when(movieRepository.findById(ID_1))
                .thenReturn(Optional.of(new Movie(ID_1, "HOME ALONE 2", "Comedy", 1992, 9.0)));
        Movie expectedMovie = new Movie(ID_1, "HOME ALONE", "Christmas movie", 1990, 8.5);
        when(movieRepository.save(expectedMovie)).thenReturn(expectedMovie);

        // when
        Movie actualMovie = movieService.update(ID_1, expectedMovie);

        // then
        assertThat(actualMovie)
                .isEqualTo(expectedMovie);
    }

    @Test
    void throws_exception_when_trying_to_update_non_existing_movie() {
        // given
        Movie movie = new Movie("HOME ALONE", "Christmas movie", 1990, 8.5);
        when(movieRepository.findById(ID_1)).thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(MovieNotFoundException.class)
                .isThrownBy(() -> movieService.update(ID_1, movie))
                .withMessage("Update failed. Could not find movie by id - " + ID_1);
    }

    private ArgumentMatcher<Movie> matchesMovieInfoAndDetailsToEntity(FindMovieInfo findMovieInfo, MovieDetails movieDetails) {
        return movie -> movie.getTitle().equals(findMovieInfo.title()) &&
                        movie.getDescription().equals(movieDetails.description()) &&
                        movie.getReleaseYear() == findMovieInfo.releaseYear() &&
                        movie.getRating() == movieDetails.rating();
    }
}
