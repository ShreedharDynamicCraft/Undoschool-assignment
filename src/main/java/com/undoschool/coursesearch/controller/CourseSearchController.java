package com.undoschool.coursesearch.controller;

import com.undoschool.coursesearch.dto.CourseSearchRequestDto;
import com.undoschool.coursesearch.dto.CourseSearchResponseDto;
import com.undoschool.coursesearch.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for course search API
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    /**
     * Search courses with filters, pagination, and sorting
     */
    @GetMapping("/search")
    public ResponseEntity<CourseSearchResponseDto> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        CourseSearchRequestDto request = CourseSearchRequestDto.builder()
            .q(q)
            .minAge(minAge)
            .maxAge(maxAge)
            .category(category)
            .type(type)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .startDate(startDate)
            .sort(sort)
            .page(page)
            .size(size)
            .build();
        
        log.info("Searching courses with parameters: {}", request);
        
        CourseSearchResponseDto response = courseSearchService.searchCourses(request);
        
        log.info("Found {} courses", response.getTotal());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get autocomplete suggestions for course titles
     */
    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {
        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Getting suggestions for query: {}", q);
        
        List<String> suggestions = courseSearchService.getSuggestions(q.trim());
        
        log.info("Found {} suggestions", suggestions.size());
        
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Course Search API is running!");
    }
}
