import { UserRoles } from './user-role.enum';
export declare class JWTAccessPayload {
    id: string;
    email: string;
    role: UserRoles;
}
