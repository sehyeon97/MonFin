import { UserRoles } from './user-role.enum';
export declare class LoginUserRequest {
    email: string;
    password: string;
    role: UserRoles;
}
