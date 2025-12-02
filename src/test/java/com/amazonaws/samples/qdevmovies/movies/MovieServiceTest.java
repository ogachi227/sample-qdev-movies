package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieService class
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMoviesWithNoCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should return movies matching name search (case insensitive)")
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("prison"));
    }

    @Test
    @DisplayName("Should return movies matching name search with different case")
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("FAMILY", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMovieName().toLowerCase().contains("family"));
    }

    @Test
    @DisplayName("Should return movie matching exact ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    @DisplayName("Should return movies matching genre search")
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should return movies matching multiple criteria")
    public void testSearchMoviesWithMultipleCriteria() {
        List<Movie> results = movieService.searchMovies("family", null, "crime");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        Movie movie = results.get(0);
        assertTrue(movie.getMovieName().toLowerCase().contains("family"));
        assertTrue(movie.getGenre().toLowerCase().contains("crime"));
    }

    @Test
    @DisplayName("Should return empty list when no movies match criteria")
    public void testSearchMoviesNoMatches() {
        List<Movie> results = movieService.searchMovies("nonexistent", null, null);
        
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Should handle empty string search parameters")
    public void testSearchMoviesWithEmptyStrings() {
        List<Movie> results = movieService.searchMovies("", null, "");
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should handle whitespace-only search parameters")
    public void testSearchMoviesWithWhitespace() {
        List<Movie> results = movieService.searchMovies("   ", null, "  ");
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should return empty list for invalid ID")
    public void testSearchMoviesWithInvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    @DisplayName("Should handle partial genre matches")
    public void testSearchMoviesPartialGenreMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "sci");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    @DisplayName("Should return correct movie by ID using getMovieById")
    public void testGetMovieById() {
        Optional<Movie> movieOpt = movieService.getMovieById(1L);
        
        assertTrue(movieOpt.isPresent());
        assertEquals(1L, movieOpt.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional for invalid ID")
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movieOpt = movieService.getMovieById(999L);
        
        assertFalse(movieOpt.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional for null ID")
    public void testGetMovieByIdNull() {
        Optional<Movie> movieOpt = movieService.getMovieById(null);
        
        assertFalse(movieOpt.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional for negative ID")
    public void testGetMovieByIdNegative() {
        Optional<Movie> movieOpt = movieService.getMovieById(-1L);
        
        assertFalse(movieOpt.isPresent());
    }

    @Test
    @DisplayName("Should return all movies from getAllMovies")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies);
        assertTrue(movies.size() > 0);
        // Verify we have the expected number of movies from the JSON file
        assertEquals(12, movies.size());
    }
}