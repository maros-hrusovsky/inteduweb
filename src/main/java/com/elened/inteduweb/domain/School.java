package com.elened.inteduweb.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A School.
 */
@Entity
@Table(name = "school")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "school")
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "school")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Classroom> classrooms = new HashSet<>();

    @OneToMany(mappedBy = "school")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public School name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Classroom> getClassrooms() {
        return classrooms;
    }

    public School classrooms(Set<Classroom> classrooms) {
        this.classrooms = classrooms;
        return this;
    }

    public School addClassrooms(Classroom classroom) {
        this.classrooms.add(classroom);
        classroom.setSchool(this);
        return this;
    }

    public School removeClassrooms(Classroom classroom) {
        this.classrooms.remove(classroom);
        classroom.setSchool(null);
        return this;
    }

    public void setClassrooms(Set<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public Set<User> getUsers() {
        return users;
    }

    public School users(Set<User> users) {
        this.users = users;
        return this;
    }

    public School addUsers(User user) {
        this.users.add(user);
        user.setSchool(this);
        return this;
    }

    public School removeUsers(User user) {
        this.users.remove(user);
        user.setSchool(null);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof School)) {
            return false;
        }
        return id != null && id.equals(((School) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "School{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
