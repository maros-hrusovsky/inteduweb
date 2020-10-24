package com.elened.inteduweb.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link ClassroomSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class ClassroomSearchRepositoryMockConfiguration {

    @MockBean
    private ClassroomSearchRepository mockClassroomSearchRepository;

}
