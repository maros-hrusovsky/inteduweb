package com.elened.inteduweb.repository.search;

import com.elened.inteduweb.domain.Classroom;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Classroom} entity.
 */
public interface ClassroomSearchRepository extends ElasticsearchRepository<Classroom, Long> {
}
