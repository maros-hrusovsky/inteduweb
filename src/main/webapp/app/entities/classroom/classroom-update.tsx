import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ISchool } from 'app/shared/model/school.model';
import { getEntities as getSchools } from 'app/entities/school/school.reducer';
import { getEntity, updateEntity, createEntity, reset } from './classroom.reducer';
import { IClassroom } from 'app/shared/model/classroom.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IClassroomUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ClassroomUpdate = (props: IClassroomUpdateProps) => {
  const [idsusers, setIdsusers] = useState([]);
  const [schoolId, setSchoolId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { classroomEntity, users, schools, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/classroom');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getUsers();
    props.getSchools();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...classroomEntity,
        ...values,
        users: mapIdList(values.users),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="inteduwebApp.classroom.home.createOrEditLabel">
            <Translate contentKey="inteduwebApp.classroom.home.createOrEditLabel">Create or edit a Classroom</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : classroomEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="classroom-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="classroom-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="classroom-name">
                  <Translate contentKey="inteduwebApp.classroom.name">Name</Translate>
                </Label>
                <AvField id="classroom-name" type="text" name="name" />
              </AvGroup>
              <AvGroup>
                <Label for="classroom-users">
                  <Translate contentKey="inteduwebApp.classroom.users">Users</Translate>
                </Label>
                <AvInput
                  id="classroom-users"
                  type="select"
                  multiple
                  className="form-control"
                  name="users"
                  value={classroomEntity.users && classroomEntity.users.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {users
                    ? users.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.firstname}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="classroom-school">
                  <Translate contentKey="inteduwebApp.classroom.school">School</Translate>
                </Label>
                <AvInput id="classroom-school" type="select" className="form-control" name="school.id">
                  <option value="" key="0" />
                  {schools
                    ? schools.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/classroom" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  schools: storeState.school.entities,
  classroomEntity: storeState.classroom.entity,
  loading: storeState.classroom.loading,
  updating: storeState.classroom.updating,
  updateSuccess: storeState.classroom.updateSuccess,
});

const mapDispatchToProps = {
  getUsers,
  getSchools,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ClassroomUpdate);
