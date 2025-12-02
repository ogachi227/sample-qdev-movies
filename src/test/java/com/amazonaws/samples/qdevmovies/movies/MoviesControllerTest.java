package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for MoviesController class
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MockMovieService mockMovieService;
    private ReviewService mockReviewService;

    // Mock MovieService for testing
    private static class MockMovieService extends MovieService {
        private final List<Movie> testMovies;

        public MockMovieService() {
            this.testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
        }

        @Override
        public List<Movie> getAllMovies() {
            return testMovies;
        }
        
        @Override
        public Optional<Movie> getMovieById(Long id) {
            return testMovies.stream().filter(movie -> movie.getId().equals(id)).findFirst();
        }

        @Override
        public List<Movie> searchMovies(String name, Long id, String genre) {
            List<Movie> results = new ArrayList<>();
            for (Movie movie : testMovies) {
                boolean matches = true;
                
                if (id != null && !movie.getId().equals(id)) {
                    matches = false;
                }
                
                if (matches && name != null && !name.trim().isEmpty()) {
                    if (!movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) {
                        matches = false;
                    }
                }
                
                if (matches && genre != null && !genre.trim().isEmpty()) {
                    if (!movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) {
                        matches = false;
                    }
                }
                
                if (matches) {
                    results.add(movie);
                }
            }
            return results;
        }
    }

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MockMovieService();
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies template for getMovies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    @DisplayName("Should return movie-details template for valid movie ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie);
        assertEquals(1L, movie.getId());
    }

    @Test
    @DisplayName("Should return error template for invalid movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertNotNull(title);
        assertNotNull(message);
        assertTrue(message.contains("999"));
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMoviesNoCriteria() {
        String result = moviesController.searchMovies(null, null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
        
        Boolean searchPerformed = (Boolean) model.getAttribute("searchPerformed");
        assertTrue(searchPerformed);
    }

    @Test
    @DisplayName("Should return filtered movies when searching by name")
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("test", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String searchName = (String) model.getAttribute("searchName");
        assertEquals("test", searchName);
        
        Integer resultCount = (Integer) model.getAttribute("resultCount");
        assertEquals(1, resultCount);
    }

    @Test
    @DisplayName("Should return filtered movies when searching by ID")
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals(2L, movies.get(0).getId());
        
        String searchId = (String) model.getAttribute("searchId");
        assertEquals("2", searchId);
    }

    @Test
    @DisplayName("Should return filtered movies when searching by genre")
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "action", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Action", movies.get(0).getGenre());
        
        String searchGenre = (String) model.getAttribute("searchGenre");
        assertEquals("action", searchGenre);
    }

    @Test
    @DisplayName("Should return empty results when no movies match search criteria")
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("nonexistent", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(0, movies.size());
        
        Integer resultCount = (Integer) model.getAttribute("resultCount");
        assertEquals(0, resultCount);
    }

    @Test
    @DisplayName("Should return error for invalid movie ID in search")
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, -1L, null, model);
        
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertNotNull(title);
        assertNotNull(message);
        assertTrue(message.contains("positive number"));
    }

    @Test
    @DisplayName("Should handle multiple search criteria")
    public void testSearchMoviesMultipleCriteria() {
        String result = moviesController.searchMovies("action", null, "action", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        
        Movie movie = movies.get(0);
        assertTrue(movie.getMovieName().toLowerCase().contains("action"));
        assertTrue(movie.getGenre().toLowerCase().contains("action"));
    }

    @Test
    @DisplayName("Should handle empty string search parameters")
    public void testSearchMoviesEmptyStrings() {
        String result = moviesController.searchMovies("", null, "", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size()); // Should return all movies
    }

    @Test
    @DisplayName("Should set search summary correctly")
    public void testSearchMoviesSearchSummary() {
        String result = moviesController.searchMovies("test", 1L, "drama", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        String searchSummary = (String) model.getAttribute("searchSummary");
        assertNotNull(searchSummary);
        assertTrue(searchSummary.contains("test"));
        assertTrue(searchSummary.contains("1"));
        assertTrue(searchSummary.contains("drama"));
    }

    @Test
    @DisplayName("Should integrate with movie service correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        Optional<Movie> movieOpt = mockMovieService.getMovieById(1L);
        assertTrue(movieOpt.isPresent());
        assertEquals("Test Movie", movieOpt.get().getMovieName());
        
        List<Movie> searchResults = mockMovieService.searchMovies("test", null, null);
        assertEquals(1, searchResults.size());
        assertEquals("Test Movie", searchResults.get(0).getMovieName());
    }
}
