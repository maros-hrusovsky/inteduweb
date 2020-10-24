package com.elened.inteduweb.repository;

import com.elened.inteduweb.domain.Classroom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Classroom entity.
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    @Query(value = "select distinct classroom from Classroom classroom left join fetch classroom.users",
        countQuery = "select count(distinct classroom) from Classroom classroom")
    Page<Classroom> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct classroom from Classroom classroom left join fetch classroom.users")
    List<Classroom> findAllWithEagerRelationships();

    @Query("select classroom from Classroom classroom left join fetch classroom.users where classroom.id =:id")
    Optional<Classroom> findOneWithEagerRelationships(@Param("id") Long id);
}
