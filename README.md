# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Movie Search**: Search and filter movies by name, ID, or genre with an intuitive web form
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Movie Search**: http://localhost:8080/movies/search (with optional query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── Movie.java                # Movie data model
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── HTMLBuilder.java      # HTML generation utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── application.yml               # Application configuration
│       ├── mock-reviews.json             # Mock review data
│       └── log4j2.xml                    # Logging configuration
└── test/                                 # Unit tests
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information. Includes a search form for filtering movies.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### Search Movies
```
GET /movies/search
```
Returns an HTML page with filtered movie results based on search criteria. Uses the same template as the movie list but shows only matching results.

**Query Parameters (all optional):**
- `name` (string): Movie name to search for (case-insensitive, partial match)
- `id` (number): Movie ID to search for (exact match)
- `genre` (string): Movie genre to search for (case-insensitive, partial match)

**Examples:**
```
http://localhost:8080/movies/search?name=prison
http://localhost:8080/movies/search?genre=drama
http://localhost:8080/movies/search?id=1
http://localhost:8080/movies/search?name=family&genre=crime
```

**Features:**
- Case-insensitive search for name and genre
- Partial matching for name and genre fields
- Exact matching for ID field
- Multiple criteria can be combined
- Empty results are handled gracefully with user-friendly messages
- Search form preserves user input after submission
- Input validation with error handling

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX
- Add new features like advanced search filters or sorting
- Improve the responsive design
- Add more comprehensive search capabilities

## Recent Updates

### Movie Search and Filtering Feature
- **New Endpoint**: `/movies/search` - Search and filter movies by name, ID, or genre
- **Enhanced UI**: Added search form with input fields for name, ID, and genre
- **Smart Filtering**: Case-insensitive partial matching for name and genre, exact matching for ID
- **User Experience**: 
  - Search form preserves user input after submission
  - Clear search functionality to reset filters
  - Friendly "no results" message with pirate-themed styling
  - Search results summary showing criteria and count
- **Error Handling**: Input validation with user-friendly error messages
- **Testing**: Comprehensive unit tests for search functionality
- **Documentation**: Updated API documentation with search examples

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
