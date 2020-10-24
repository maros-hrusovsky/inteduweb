import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Classroom from './classroom';
import ClassroomDetail from './classroom-detail';
import ClassroomUpdate from './classroom-update';
import ClassroomDeleteDialog from './classroom-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ClassroomUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ClassroomUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ClassroomDetail} />
      <ErrorBoundaryRoute path={match.url} component={Classroom} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ClassroomDeleteDialog} />
  </>
);

export default Routes;
