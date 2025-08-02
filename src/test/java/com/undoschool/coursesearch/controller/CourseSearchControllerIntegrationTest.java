package com.undoschool.coursesearch.controller;

import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Testcontainers
public class CourseSearchControllerIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer = 
        new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("discovery.type", "single-node");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        
        // Create test data
        CourseDocument course1 = CourseDocument.builder()
            .id("test1")
            .title("Java Programming")
            .description("Learn Java programming from basics")
            .category("Technology")
            .type(CourseDocument.CourseType.COURSE)
            .gradeRange("6th-12th")
            .minAge(11)
            .maxAge(18)
            .price(new BigDecimal("250.00"))
            .nextSessionDate(LocalDateTime.of(2025, 8, 15, 10, 0))
            .build();

        CourseDocument course2 = CourseDocument.builder()
            .id("test2")
            .title("Art Workshop")
            .description("Creative art and painting")
            .category("Art")
            .type(CourseDocument.CourseType.ONE_TIME)
            .gradeRange("K-5th")
            .minAge(5)
            .maxAge(11)
            .price(new BigDecimal("75.00"))
            .nextSessionDate(LocalDateTime.of(2025, 8, 20, 14, 0))
            .build();

        courseRepository.save(course1);
        courseRepository.save(course2);
        
        // Refresh index to make data searchable immediately
        elasticsearchOperations.indexOps(CourseDocument.class).refresh();
    }

    @Test
    void testSearchCoursesWithKeyword() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("q", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Java Programming"));
    }

    @Test
    void testSearchCoursesWithCategory() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("category", "Art"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Art Workshop"));
    }

    @Test
    void testSearchCoursesWithPriceRange() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("minPrice", "50")
                .param("maxPrice", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Art Workshop"));
    }

    @Test
    void testSearchCoursesWithPagination() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(1));
    }

    @Test
    void testGetSuggestions() throws Exception {
        mockMvc.perform(get("/api/search/suggest")
                .param("q", "Ja"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("Java Programming"));
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course Search API is running!"));
    }
}
