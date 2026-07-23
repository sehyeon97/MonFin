import { UserRoles } from './user-role.enum';

export class JWTAccessPayload {
    id!: string; // user id
    email!: string;
    role!: UserRoles;
}
