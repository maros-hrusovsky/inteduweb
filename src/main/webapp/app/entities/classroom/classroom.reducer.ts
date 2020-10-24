import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IClassroom, defaultValue } from 'app/shared/model/classroom.model';

export const ACTION_TYPES = {
  SEARCH_CLASSROOMS: 'classroom/SEARCH_CLASSROOMS',
  FETCH_CLASSROOM_LIST: 'classroom/FETCH_CLASSROOM_LIST',
  FETCH_CLASSROOM: 'classroom/FETCH_CLASSROOM',
  CREATE_CLASSROOM: 'classroom/CREATE_CLASSROOM',
  UPDATE_CLASSROOM: 'classroom/UPDATE_CLASSROOM',
  DELETE_CLASSROOM: 'classroom/DELETE_CLASSROOM',
  RESET: 'classroom/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IClassroom>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ClassroomState = Readonly<typeof initialState>;

// Reducer

export default (state: ClassroomState = initialState, action): ClassroomState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_CLASSROOMS):
    case REQUEST(ACTION_TYPES.FETCH_CLASSROOM_LIST):
    case REQUEST(ACTION_TYPES.FETCH_CLASSROOM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_CLASSROOM):
    case REQUEST(ACTION_TYPES.UPDATE_CLASSROOM):
    case REQUEST(ACTION_TYPES.DELETE_CLASSROOM):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_CLASSROOMS):
    case FAILURE(ACTION_TYPES.FETCH_CLASSROOM_LIST):
    case FAILURE(ACTION_TYPES.FETCH_CLASSROOM):
    case FAILURE(ACTION_TYPES.CREATE_CLASSROOM):
    case FAILURE(ACTION_TYPES.UPDATE_CLASSROOM):
    case FAILURE(ACTION_TYPES.DELETE_CLASSROOM):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_CLASSROOMS):
    case SUCCESS(ACTION_TYPES.FETCH_CLASSROOM_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_CLASSROOM):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_CLASSROOM):
    case SUCCESS(ACTION_TYPES.UPDATE_CLASSROOM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_CLASSROOM):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/classrooms';
const apiSearchUrl = 'api/_search/classrooms';

// Actions

export const getSearchEntities: ICrudSearchAction<IClassroom> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_CLASSROOMS,
  payload: axios.get<IClassroom>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IClassroom> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_CLASSROOM_LIST,
  payload: axios.get<IClassroom>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IClassroom> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_CLASSROOM,
    payload: axios.get<IClassroom>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IClassroom> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_CLASSROOM,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IClassroom> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_CLASSROOM,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IClassroom> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_CLASSROOM,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
