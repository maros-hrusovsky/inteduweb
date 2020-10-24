package com.elened.inteduweb.repository.search;

import com.elened.inteduweb.domain.School;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link School} entity.
 */
public interface SchoolSearchRepository extends ElasticsearchRepository<School, Long> {
}
