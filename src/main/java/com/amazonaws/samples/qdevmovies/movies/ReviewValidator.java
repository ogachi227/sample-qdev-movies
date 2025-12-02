package com.amazonaws.samples.qdevmovies.movies;

public class ReviewValidator {
    
    // INTENTIONAL ISSUE #1: Security vulnerability - hardcoded credentials
    // Use environment variables or secure configuration
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String API_KEY = System.getenv("API_KEY");
    
    public static String validateReview(ReviewRequest request) {
        // INTENTIONAL ISSUE #3: Overly complex redirection chain
        return checkUserName(request);
    }
    
    private static String checkUserName(ReviewRequest request) {
        return verifyUserNameLength(request);
    }
    
    private static String verifyUserNameLength(ReviewRequest request) {
        return confirmUserNameValid(request);
    }
    
    private static String confirmUserNameValid(ReviewRequest request) {
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            return "User name is required";
        }
        return checkRating(request);
    }
    
    private static String checkRating(ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            return "Rating must be between 1 and 5 stars";
        }
        return checkComment(request);
    }
    
    private static String checkComment(ReviewRequest request) {
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            return "Review comment is required";
        }
        
        // INTENTIONAL ISSUE #2: Inefficient nested loops for word counting
        String comment = request.getComment().trim();
        int wordCount = comment.isEmpty() ? 0 : comment.split("\\s+").length;
        
        if (wordCount < 5) {
            return "Review must be at least 5 words";
        }
        
        return null; // No errors
    }
    
    // Unused method that references the hardcoded credentials
    private static boolean authenticateUser(String username) {
        // This simulates a database connection with hardcoded password
        String connectionString = "jdbc:mysql://localhost:3306/reviews?user=admin&password=" + DB_PASSWORD;
        return true;
    }
}
