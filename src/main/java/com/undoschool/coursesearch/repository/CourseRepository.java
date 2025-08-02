package com.undoschool.coursesearch.repository;

import com.undoschool.coursesearch.document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch repository for CourseDocument
 */
@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
