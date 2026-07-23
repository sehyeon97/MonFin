import { IsNotEmpty } from 'class-validator';

export class SignInCustomerRequest {
    @IsNotEmpty()
    email!: string;

    @IsNotEmpty()
    password!: string;
}
