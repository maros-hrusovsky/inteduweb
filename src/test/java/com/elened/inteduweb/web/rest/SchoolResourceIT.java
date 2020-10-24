package com.elened.inteduweb.web.rest;

import com.elened.inteduweb.InteduwebApp;
import com.elened.inteduweb.domain.School;
import com.elened.inteduweb.repository.SchoolRepository;
import com.elened.inteduweb.repository.search.SchoolSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SchoolResource} REST controller.
 */
@SpringBootTest(classes = InteduwebApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class SchoolResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private SchoolRepository schoolRepository;

    /**
     * This repository is mocked in the com.elened.inteduweb.repository.search test package.
     *
     * @see com.elened.inteduweb.repository.search.SchoolSearchRepositoryMockConfiguration
     */
    @Autowired
    private SchoolSearchRepository mockSchoolSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSchoolMockMvc;

    private School school;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static School createEntity(EntityManager em) {
        School school = new School()
            .name(DEFAULT_NAME);
        return school;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static School createUpdatedEntity(EntityManager em) {
        School school = new School()
            .name(UPDATED_NAME);
        return school;
    }

    @BeforeEach
    public void initTest() {
        school = createEntity(em);
    }

    @Test
    @Transactional
    public void createSchool() throws Exception {
        int databaseSizeBeforeCreate = schoolRepository.findAll().size();
        // Create the School
        restSchoolMockMvc.perform(post("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(school)))
            .andExpect(status().isCreated());

        // Validate the School in the database
        List<School> schoolList = schoolRepository.findAll();
        assertThat(schoolList).hasSize(databaseSizeBeforeCreate + 1);
        School testSchool = schoolList.get(schoolList.size() - 1);
        assertThat(testSchool.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the School in Elasticsearch
        verify(mockSchoolSearchRepository, times(1)).save(testSchool);
    }

    @Test
    @Transactional
    public void createSchoolWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = schoolRepository.findAll().size();

        // Create the School with an existing ID
        school.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchoolMockMvc.perform(post("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(school)))
            .andExpect(status().isBadRequest());

        // Validate the School in the database
        List<School> schoolList = schoolRepository.findAll();
        assertThat(schoolList).hasSize(databaseSizeBeforeCreate);

        // Validate the School in Elasticsearch
        verify(mockSchoolSearchRepository, times(0)).save(school);
    }


    @Test
    @Transactional
    public void getAllSchools() throws Exception {
        // Initialize the database
        schoolRepository.saveAndFlush(school);

        // Get all the schoolList
        restSchoolMockMvc.perform(get("/api/schools?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(school.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getSchool() throws Exception {
        // Initialize the database
        schoolRepository.saveAndFlush(school);

        // Get the school
        restSchoolMockMvc.perform(get("/api/schools/{id}", school.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(school.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingSchool() throws Exception {
        // Get the school
        restSchoolMockMvc.perform(get("/api/schools/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchool() throws Exception {
        // Initialize the database
        schoolRepository.saveAndFlush(school);

        int databaseSizeBeforeUpdate = schoolRepository.findAll().size();

        // Update the school
        School updatedSchool = schoolRepository.findById(school.getId()).get();
        // Disconnect from session so that the updates on updatedSchool are not directly saved in db
        em.detach(updatedSchool);
        updatedSchool
            .name(UPDATED_NAME);

        restSchoolMockMvc.perform(put("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSchool)))
            .andExpect(status().isOk());

        // Validate the School in the database
        List<School> schoolList = schoolRepository.findAll();
        assertThat(schoolList).hasSize(databaseSizeBeforeUpdate);
        School testSchool = schoolList.get(schoolList.size() - 1);
        assertThat(testSchool.getName()).isEqualTo(UPDATED_NAME);

        // Validate the School in Elasticsearch
        verify(mockSchoolSearchRepository, times(1)).save(testSchool);
    }

    @Test
    @Transactional
    public void updateNonExistingSchool() throws Exception {
        int databaseSizeBeforeUpdate = schoolRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchoolMockMvc.perform(put("/api/schools")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(school)))
            .andExpect(status().isBadRequest());

        // Validate the School in the database
        List<School> schoolList = schoolRepository.findAll();
        assertThat(schoolList).hasSize(databaseSizeBeforeUpdate);

        // Validate the School in Elasticsearch
        verify(mockSchoolSearchRepository, times(0)).save(school);
    }

    @Test
    @Transactional
    public void deleteSchool() throws Exception {
        // Initialize the database
        schoolRepository.saveAndFlush(school);

        int databaseSizeBeforeDelete = schoolRepository.findAll().size();

        // Delete the school
        restSchoolMockMvc.perform(delete("/api/schools/{id}", school.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<School> schoolList = schoolRepository.findAll();
        assertThat(schoolList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the School in Elasticsearch
        verify(mockSchoolSearchRepository, times(1)).deleteById(school.getId());
    }

    @Test
    @Transactional
    public void searchSchool() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        schoolRepository.saveAndFlush(school);
        when(mockSchoolSearchRepository.search(queryStringQuery("id:" + school.getId())))
            .thenReturn(Collections.singletonList(school));

        // Search the school
        restSchoolMockMvc.perform(get("/api/_search/schools?query=id:" + school.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(school.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
