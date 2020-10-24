import { IClassroom } from 'app/shared/model/classroom.model';
import { IUser } from 'app/shared/model/user.model';

export interface ISchool {
  id?: number;
  name?: string;
  classrooms?: IClassroom[];
  users?: IUser[];
}

export const defaultValue: Readonly<ISchool> = {};
