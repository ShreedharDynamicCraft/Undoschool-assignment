package com.undoschool.coursesearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for loading sample course data into Elasticsearch
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoaderService implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            log.info("Loading sample course data...");
            loadSampleData();
            log.info("Sample course data loaded successfully. Total courses: {}", courseRepository.count());
        } else {
            log.info("Course data already exists. Skipping data loading. Total courses: {}", courseRepository.count());
        }
    }

    private void loadSampleData() throws IOException {
        ClassPathResource resource = new ClassPathResource("sample-courses.json");
        
        try (InputStream inputStream = resource.getInputStream()) {
            List<CourseDocument> courses = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<CourseDocument>>() {}
            );
            
            // Add completion suggestions for autocomplete
            courses = courses.stream()
                .map(this::addCompletionSuggestion)
                .collect(Collectors.toList());
            
            courseRepository.saveAll(courses);
            log.info("Loaded {} courses into Elasticsearch", courses.size());
        }
    }
    
    private CourseDocument addCompletionSuggestion(CourseDocument course) {
        // Create completion suggestion from title
        Completion suggestion = new Completion(new String[]{course.getTitle()});
        suggestion.setWeight(1);
        course.setSuggest(suggestion);
        return course;
    }
}
