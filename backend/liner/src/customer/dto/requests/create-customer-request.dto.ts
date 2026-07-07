import { IsEmail, IsNotEmpty, IsStrongPassword, Length } from 'class-validator';

export class CreateCustomerRequest {
    @IsNotEmpty()
    @IsEmail()
    email!: string;

    @IsStrongPassword()
    password!: string;

    @IsNotEmpty()
    billingAddress!: string;

    @IsNotEmpty()
    billingCity!: string;

    @IsNotEmpty()
    @Length(2, 2)
    billingState!: string;

    @IsNotEmpty()
    billingZip!: string;
}
