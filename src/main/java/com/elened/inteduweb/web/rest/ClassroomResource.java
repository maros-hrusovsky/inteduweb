package com.elened.inteduweb.web.rest;

import com.elened.inteduweb.domain.Classroom;
import com.elened.inteduweb.repository.ClassroomRepository;
import com.elened.inteduweb.repository.search.ClassroomSearchRepository;
import com.elened.inteduweb.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.elened.inteduweb.domain.Classroom}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClassroomResource {

    private final Logger log = LoggerFactory.getLogger(ClassroomResource.class);

    private static final String ENTITY_NAME = "classroom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClassroomRepository classroomRepository;

    private final ClassroomSearchRepository classroomSearchRepository;

    public ClassroomResource(ClassroomRepository classroomRepository, ClassroomSearchRepository classroomSearchRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomSearchRepository = classroomSearchRepository;
    }

    /**
     * {@code POST  /classrooms} : Create a new classroom.
     *
     * @param classroom the classroom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classroom, or with status {@code 400 (Bad Request)} if the classroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/classrooms")
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) throws URISyntaxException {
        log.debug("REST request to save Classroom : {}", classroom);
        if (classroom.getId() != null) {
            throw new BadRequestAlertException("A new classroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Classroom result = classroomRepository.save(classroom);
        classroomSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/classrooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /classrooms} : Updates an existing classroom.
     *
     * @param classroom the classroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroom,
     * or with status {@code 400 (Bad Request)} if the classroom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/classrooms")
    public ResponseEntity<Classroom> updateClassroom(@RequestBody Classroom classroom) throws URISyntaxException {
        log.debug("REST request to update Classroom : {}", classroom);
        if (classroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Classroom result = classroomRepository.save(classroom);
        classroomSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classroom.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /classrooms} : get all the classrooms.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classrooms in body.
     */
    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Classrooms");
        return classroomRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /classrooms/:id} : get the "id" classroom.
     *
     * @param id the id of the classroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> getClassroom(@PathVariable Long id) {
        log.debug("REST request to get Classroom : {}", id);
        Optional<Classroom> classroom = classroomRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(classroom);
    }

    /**
     * {@code DELETE  /classrooms/:id} : delete the "id" classroom.
     *
     * @param id the id of the classroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/classrooms/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        log.debug("REST request to delete Classroom : {}", id);
        classroomRepository.deleteById(id);
        classroomSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/classrooms?query=:query} : search for the classroom corresponding
     * to the query.
     *
     * @param query the query of the classroom search.
     * @return the result of the search.
     */
    @GetMapping("/_search/classrooms")
    public List<Classroom> searchClassrooms(@RequestParam String query) {
        log.debug("REST request to search Classrooms for query {}", query);
        return StreamSupport
            .stream(classroomSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
