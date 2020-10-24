import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './classroom.reducer';
import { IClassroom } from 'app/shared/model/classroom.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IClassroomDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ClassroomDetail = (props: IClassroomDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { classroomEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="inteduwebApp.classroom.detail.title">Classroom</Translate> [<b>{classroomEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="name">
              <Translate contentKey="inteduwebApp.classroom.name">Name</Translate>
            </span>
          </dt>
          <dd>{classroomEntity.name}</dd>
          <dt>
            <Translate contentKey="inteduwebApp.classroom.users">Users</Translate>
          </dt>
          <dd>
            {classroomEntity.users
              ? classroomEntity.users.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.firstname}</a>
                    {classroomEntity.users && i === classroomEntity.users.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}{' '}
          </dd>
          <dt>
            <Translate contentKey="inteduwebApp.classroom.school">School</Translate>
          </dt>
          <dd>{classroomEntity.school ? classroomEntity.school.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/classroom" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/classroom/${classroomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ classroom }: IRootState) => ({
  classroomEntity: classroom.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ClassroomDetail);
