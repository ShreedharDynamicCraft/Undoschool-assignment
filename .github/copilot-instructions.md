# GitHub Copilot Instructions for Course Search API

<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

## Project Context
This is a Spring Boot application that provides a course search API using Elasticsearch. The project implements advanced search functionality with filters, pagination, sorting, autocomplete, and fuzzy search capabilities.

## Code Style Guidelines

### General Principles
- Follow clean code principles and SOLID design patterns
- Use meaningful variable and method names that clearly express intent
- Keep methods small and focused on a single responsibility
- Prefer composition over inheritance
- Use dependency injection consistently

### Java Specific
- Use Java 17+ features where appropriate (records, switch expressions, etc.)
- Follow Spring Boot best practices and conventions
- Use Lombok annotations to reduce boilerplate code
- Implement proper exception handling with custom exceptions
- Use validation annotations for input validation

### Spring Boot Best Practices
- Use `@Service`, `@Repository`, `@Controller` annotations appropriately
- Implement proper separation of concerns (Controller → Service → Repository)
- Use DTOs for API requests/responses to avoid exposing internal entities
- Implement proper transaction management where needed
- Use Spring's dependency injection instead of manual object creation

### Elasticsearch Integration
- Use Spring Data Elasticsearch for repository operations
- Implement efficient queries using the native Elasticsearch client
- Use proper mapping annotations for document fields
- Implement bulk operations for data loading
- Use completion suggester for autocomplete functionality

### API Design
- Follow RESTful API conventions
- Use proper HTTP status codes
- Implement comprehensive request/response DTOs
- Add proper validation and error handling
- Use meaningful endpoint paths and parameter names

### Testing
- Write unit tests for service layer logic
- Use integration tests with Testcontainers for Elasticsearch operations
- Mock external dependencies in unit tests
- Ensure good test coverage for critical functionality
- Use descriptive test method names that explain what is being tested

### Documentation
- Add comprehensive JavaDoc comments for public methods
- Use meaningful commit messages
- Keep README.md updated with setup and usage instructions
- Document API endpoints with examples

### Error Handling
- Create custom exception classes for different error scenarios
- Use `@ControllerAdvice` for global exception handling
- Return meaningful error messages with proper HTTP status codes
- Log errors appropriately without exposing sensitive information

### Performance Considerations
- Use pagination for large result sets
- Implement efficient Elasticsearch queries with proper filters
- Use bulk operations for data modifications
- Consider caching for frequently accessed data
- Optimize database queries and avoid N+1 problems

### Security
- Validate all input parameters
- Use proper authentication and authorization where needed
- Sanitize user inputs to prevent injection attacks
- Don't expose sensitive information in error messages

## Code Examples

### Controller Pattern
```java
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    
    private final CourseService courseService;
    
    @GetMapping("/search")
    public ResponseEntity<CourseSearchResponse> searchCourses(
            @Valid CourseSearchRequest request) {
        log.info("Searching courses with criteria: {}", request);
        CourseSearchResponse response = courseService.searchCourses(request);
        return ResponseEntity.ok(response);
    }
}
```

### Service Pattern
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    
    @Transactional(readOnly = true)
    public CourseSearchResponse searchCourses(CourseSearchRequest request) {
        // Implementation with proper error handling
    }
}
```

### Exception Handling
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
}
```

## Specific Project Guidelines

### Elasticsearch Queries
- Use the new Elasticsearch client APIs
- Implement proper query builders for complex searches
- Use filters instead of queries for exact matches
- Implement fuzzy search with appropriate fuzziness levels

### Data Loading
- Use bulk operations for initial data loading
- Implement proper error handling for data loading failures
- Log progress during data loading operations
- Use CommandLineRunner for startup data loading

### Search Features
- Implement multi-field searches with proper boosting
- Use range queries for numeric and date filters
- Implement proper sorting with multiple options
- Add autocomplete using completion suggester

Remember to always consider the user experience, maintainability, and performance when writing code. Use these guidelines to ensure consistency across the codebase.
