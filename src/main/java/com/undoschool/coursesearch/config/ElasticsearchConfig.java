package com.undoschool.coursesearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * Elasticsearch configuration
 */
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Override
    public String getClientConfiguration() {
        return "localhost:9200";
    }
}
