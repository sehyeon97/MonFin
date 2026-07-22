import { IsBoolean, IsEmail, IsNotEmpty, Length } from 'class-validator';

export class CreateCustomerRequest {
    @IsNotEmpty()
    @IsEmail()
    email!: string;

    @IsNotEmpty()
    //@IsStrongPassword()
    password!: string;

    @IsNotEmpty()
    @IsBoolean()
    verified!: boolean;

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
