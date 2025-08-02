package com.undoschool.coursesearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for course search response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchResponseDto {
    private long total;
    private int page;
    private int size;
    private List<CourseResponseDto> courses;
}
