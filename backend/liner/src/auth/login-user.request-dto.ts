import { IsNotEmpty } from 'class-validator';
import { UserRoles } from './user-role.enum';

export class LoginUserRequest {
    @IsNotEmpty()
    email!: string;

    @IsNotEmpty()
    password!: string;

    @IsNotEmpty()
    role!: UserRoles;
}
