package com.undoschool.coursesearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for course search request parameters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchRequestDto {
    private String q; // search keyword
    private Integer minAge;
    private Integer maxAge;
    private String category;
    private String type;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private LocalDateTime startDate;
    private String sort; // upcoming, priceAsc, priceDesc
    private Integer page;
    private Integer size;
}
