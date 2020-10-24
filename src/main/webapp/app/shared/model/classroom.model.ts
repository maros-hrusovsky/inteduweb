import { IUser } from 'app/shared/model/user.model';
import { ISchool } from 'app/shared/model/school.model';

export interface IClassroom {
  id?: number;
  name?: string;
  users?: IUser[];
  school?: ISchool;
}

export const defaultValue: Readonly<IClassroom> = {};
