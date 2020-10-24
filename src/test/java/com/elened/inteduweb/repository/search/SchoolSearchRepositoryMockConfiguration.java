package com.elened.inteduweb.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link SchoolSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SchoolSearchRepositoryMockConfiguration {

    @MockBean
    private SchoolSearchRepository mockSchoolSearchRepository;

}
