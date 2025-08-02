package com.undoschool.coursesearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.json.JsonData;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.dto.CourseResponseDto;
import com.undoschool.coursesearch.dto.CourseSearchRequestDto;
import com.undoschool.coursesearch.dto.CourseSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching courses in Elasticsearch
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Search courses with filters, pagination, and sorting
     */
    public CourseSearchResponseDto searchCourses(CourseSearchRequestDto request) {
        // Build the query
        Query query = buildSearchQuery(request);
        
        // Create pageable
        Pageable pageable = PageRequest.of(
            request.getPage() != null ? request.getPage() : 0,
            request.getSize() != null ? request.getSize() : 10
        );
        
        // Build native query with sorting
        NativeQuery.NativeQueryBuilder queryBuilder = NativeQuery.builder()
            .withQuery(query)
            .withPageable(pageable);
        
        // Add sorting
        addSorting(queryBuilder, request.getSort());
        
        // Execute search
        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(
            queryBuilder.build(), CourseDocument.class
        );
        
        // Convert results
        List<CourseResponseDto> courses = searchHits.getSearchHits().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
        
        return CourseSearchResponseDto.builder()
            .total(searchHits.getTotalHits())
            .page(request.getPage() != null ? request.getPage() : 0)
            .size(request.getSize() != null ? request.getSize() : 10)
            .courses(courses)
            .build();
    }

    /**
     * Get autocomplete suggestions for course titles
     */
    public List<String> getSuggestions(String query) {
        // Implementation for autocomplete using completion suggester
        // This is a simplified version - in production, you'd use the completion suggester API
        Query searchQuery = Query.of(q -> q
            .multiMatch(m -> m
                .query(query)
                .fields("title^2", "description")
                .fuzziness("AUTO")
            )
        );
        
        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(searchQuery)
            .withMaxResults(10)
            .build();
        
        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(
            nativeQuery, CourseDocument.class
        );
        
        return searchHits.getSearchHits().stream()
            .map(hit -> hit.getContent().getTitle())
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }

    private Query buildSearchQuery(CourseSearchRequestDto request) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        
        // Full-text search on title and description
        if (request.getQ() != null && !request.getQ().trim().isEmpty()) {
            Query textQuery = Query.of(q -> q
                .multiMatch(m -> m
                    .query(request.getQ())
                    .fields("title^2", "description")
                    .fuzziness("AUTO") // Enable fuzzy matching
                )
            );
            boolQuery.must(textQuery);
        }
        
        // Age range filters
        if (request.getMinAge() != null || request.getMaxAge() != null) {
            addAgeRangeFilter(boolQuery, request.getMinAge(), request.getMaxAge());
        }
        
        // Category filter
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            Query categoryQuery = Query.of(q -> q
                .term(t -> t
                    .field("category")
                    .value(request.getCategory())
                )
            );
            boolQuery.filter(categoryQuery);
        }
        
        // Type filter
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            Query typeQuery = Query.of(q -> q
                .term(t -> t
                    .field("type")
                    .value(request.getType())
                )
            );
            boolQuery.filter(typeQuery);
        }
        
        // Price range filter
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            addPriceRangeFilter(boolQuery, request.getMinPrice(), request.getMaxPrice());
        }
        
        // Date filter (courses on or after the given date)
        if (request.getStartDate() != null) {
            addDateFilter(boolQuery, request.getStartDate());
        }
        
        return Query.of(q -> q.bool(boolQuery.build()));
    }
    
    private void addAgeRangeFilter(BoolQuery.Builder boolQuery, Integer minAge, Integer maxAge) {
        if (minAge != null) {
            // Course's maxAge should be >= user's minAge
            Query minAgeQuery = Query.of(q -> q
                .range(r -> r
                    .field("maxAge")
                    .gte(JsonData.of(minAge))
                )
            );
            boolQuery.filter(minAgeQuery);
        }
        
        if (maxAge != null) {
            // Course's minAge should be <= user's maxAge
            Query maxAgeQuery = Query.of(q -> q
                .range(r -> r
                    .field("minAge")
                    .lte(JsonData.of(maxAge))
                )
            );
            boolQuery.filter(maxAgeQuery);
        }
    }
    
    private void addPriceRangeFilter(BoolQuery.Builder boolQuery, BigDecimal minPrice, BigDecimal maxPrice) {
        RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field("price");
        
        if (minPrice != null) {
            rangeBuilder.gte(JsonData.of(minPrice));
        }
        
        if (maxPrice != null) {
            rangeBuilder.lte(JsonData.of(maxPrice));
        }
        
        Query priceQuery = Query.of(q -> q.range(rangeBuilder.build()));
        boolQuery.filter(priceQuery);
    }
    
    private void addDateFilter(BoolQuery.Builder boolQuery, LocalDateTime startDate) {
        Query dateQuery = Query.of(q -> q
            .range(r -> r
                .field("nextSessionDate")
                .gte(JsonData.of(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            )
        );
        boolQuery.filter(dateQuery);
    }
    
    private void addSorting(NativeQuery.NativeQueryBuilder queryBuilder, String sort) {
        if (sort == null || sort.equals("upcoming")) {
            // Default sort: ascending by nextSessionDate
            queryBuilder.withSort(s -> s
                .field(f -> f
                    .field("nextSessionDate")
                    .order(SortOrder.Asc)
                )
            );
        } else if (sort.equals("priceAsc")) {
            queryBuilder.withSort(s -> s
                .field(f -> f
                    .field("price")
                    .order(SortOrder.Asc)
                )
            );
        } else if (sort.equals("priceDesc")) {
            queryBuilder.withSort(s -> s
                .field(f -> f
                    .field("price")
                    .order(SortOrder.Desc)
                )
            );
        }
    }
    
    private CourseResponseDto convertToResponseDto(SearchHit<CourseDocument> hit) {
        CourseDocument course = hit.getContent();
        return CourseResponseDto.builder()
            .id(course.getId())
            .title(course.getTitle())
            .description(course.getDescription())
            .category(course.getCategory())
            .type(course.getType().toString())
            .gradeRange(course.getGradeRange())
            .minAge(course.getMinAge())
            .maxAge(course.getMaxAge())
            .price(course.getPrice())
            .nextSessionDate(course.getNextSessionDate())
            .build();
    }
}
