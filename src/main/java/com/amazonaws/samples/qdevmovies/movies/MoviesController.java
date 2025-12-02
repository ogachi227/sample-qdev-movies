package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Handle movie search requests
     * @param name Movie name search parameter (optional)
     * @param id Movie ID search parameter (optional)
     * @param genre Movie genre search parameter (optional)
     * @param model Spring MVC model
     * @return Template name for search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Movie search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                model.addAttribute("title", "Invalid Search Parameters");
                model.addAttribute("message", "Movie ID must be a positive number.");
                return "error";
            }
            
            // Perform search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Add search results and parameters to model
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchName", name != null ? name : "");
            model.addAttribute("searchId", id != null ? id.toString() : "");
            model.addAttribute("searchGenre", genre != null ? genre : "");
            model.addAttribute("searchPerformed", true);
            model.addAttribute("resultCount", searchResults.size());
            
            // Add search criteria summary for display
            StringBuilder searchSummary = new StringBuilder("Search results");
            if (name != null && !name.trim().isEmpty()) {
                searchSummary.append(" for name containing '").append(name.trim()).append("'");
            }
            if (id != null) {
                searchSummary.append(" with ID ").append(id);
            }
            if (genre != null && !genre.trim().isEmpty()) {
                searchSummary.append(" in genre '").append(genre.trim()).append("'");
            }
            model.addAttribute("searchSummary", searchSummary.toString());
            
            logger.info("Search completed successfully, found {} movies", searchResults.size());
            return "movies";
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search: {}", e.getMessage(), e);
            model.addAttribute("title", "Search Error");
            model.addAttribute("message", "An error occurred while searching for movies. Please try again.");
            return "error";
        }
    }
}