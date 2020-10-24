package com.elened.inteduweb.web.rest;

import com.elened.inteduweb.InteduwebApp;
import com.elened.inteduweb.domain.Classroom;
import com.elened.inteduweb.repository.ClassroomRepository;
import com.elened.inteduweb.repository.search.ClassroomSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ClassroomResource} REST controller.
 */
@SpringBootTest(classes = InteduwebApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ClassroomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ClassroomRepository classroomRepository;

    @Mock
    private ClassroomRepository classroomRepositoryMock;

    /**
     * This repository is mocked in the com.elened.inteduweb.repository.search test package.
     *
     * @see com.elened.inteduweb.repository.search.ClassroomSearchRepositoryMockConfiguration
     */
    @Autowired
    private ClassroomSearchRepository mockClassroomSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassroomMockMvc;

    private Classroom classroom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classroom createEntity(EntityManager em) {
        Classroom classroom = new Classroom()
            .name(DEFAULT_NAME);
        return classroom;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Classroom createUpdatedEntity(EntityManager em) {
        Classroom classroom = new Classroom()
            .name(UPDATED_NAME);
        return classroom;
    }

    @BeforeEach
    public void initTest() {
        classroom = createEntity(em);
    }

    @Test
    @Transactional
    public void createClassroom() throws Exception {
        int databaseSizeBeforeCreate = classroomRepository.findAll().size();
        // Create the Classroom
        restClassroomMockMvc.perform(post("/api/classrooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(classroom)))
            .andExpect(status().isCreated());

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll();
        assertThat(classroomList).hasSize(databaseSizeBeforeCreate + 1);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Classroom in Elasticsearch
        verify(mockClassroomSearchRepository, times(1)).save(testClassroom);
    }

    @Test
    @Transactional
    public void createClassroomWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = classroomRepository.findAll().size();

        // Create the Classroom with an existing ID
        classroom.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassroomMockMvc.perform(post("/api/classrooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(classroom)))
            .andExpect(status().isBadRequest());

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll();
        assertThat(classroomList).hasSize(databaseSizeBeforeCreate);

        // Validate the Classroom in Elasticsearch
        verify(mockClassroomSearchRepository, times(0)).save(classroom);
    }


    @Test
    @Transactional
    public void getAllClassrooms() throws Exception {
        // Initialize the database
        classroomRepository.saveAndFlush(classroom);

        // Get all the classroomList
        restClassroomMockMvc.perform(get("/api/classrooms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllClassroomsWithEagerRelationshipsIsEnabled() throws Exception {
        when(classroomRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassroomMockMvc.perform(get("/api/classrooms?eagerload=true"))
            .andExpect(status().isOk());

        verify(classroomRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllClassroomsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(classroomRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClassroomMockMvc.perform(get("/api/classrooms?eagerload=true"))
            .andExpect(status().isOk());

        verify(classroomRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getClassroom() throws Exception {
        // Initialize the database
        classroomRepository.saveAndFlush(classroom);

        // Get the classroom
        restClassroomMockMvc.perform(get("/api/classrooms/{id}", classroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classroom.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingClassroom() throws Exception {
        // Get the classroom
        restClassroomMockMvc.perform(get("/api/classrooms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClassroom() throws Exception {
        // Initialize the database
        classroomRepository.saveAndFlush(classroom);

        int databaseSizeBeforeUpdate = classroomRepository.findAll().size();

        // Update the classroom
        Classroom updatedClassroom = classroomRepository.findById(classroom.getId()).get();
        // Disconnect from session so that the updates on updatedClassroom are not directly saved in db
        em.detach(updatedClassroom);
        updatedClassroom
            .name(UPDATED_NAME);

        restClassroomMockMvc.perform(put("/api/classrooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedClassroom)))
            .andExpect(status().isOk());

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);
        Classroom testClassroom = classroomList.get(classroomList.size() - 1);
        assertThat(testClassroom.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Classroom in Elasticsearch
        verify(mockClassroomSearchRepository, times(1)).save(testClassroom);
    }

    @Test
    @Transactional
    public void updateNonExistingClassroom() throws Exception {
        int databaseSizeBeforeUpdate = classroomRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassroomMockMvc.perform(put("/api/classrooms")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(classroom)))
            .andExpect(status().isBadRequest());

        // Validate the Classroom in the database
        List<Classroom> classroomList = classroomRepository.findAll();
        assertThat(classroomList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Classroom in Elasticsearch
        verify(mockClassroomSearchRepository, times(0)).save(classroom);
    }

    @Test
    @Transactional
    public void deleteClassroom() throws Exception {
        // Initialize the database
        classroomRepository.saveAndFlush(classroom);

        int databaseSizeBeforeDelete = classroomRepository.findAll().size();

        // Delete the classroom
        restClassroomMockMvc.perform(delete("/api/classrooms/{id}", classroom.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Classroom> classroomList = classroomRepository.findAll();
        assertThat(classroomList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Classroom in Elasticsearch
        verify(mockClassroomSearchRepository, times(1)).deleteById(classroom.getId());
    }

    @Test
    @Transactional
    public void searchClassroom() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        classroomRepository.saveAndFlush(classroom);
        when(mockClassroomSearchRepository.search(queryStringQuery("id:" + classroom.getId())))
            .thenReturn(Collections.singletonList(classroom));

        // Search the classroom
        restClassroomMockMvc.perform(get("/api/_search/classrooms?query=id:" + classroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
