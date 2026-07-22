import { IsEmail, IsNotEmpty, IsStrongPassword } from 'class-validator';

export class RegisterMerchantRequest {
    @IsNotEmpty()
    @IsEmail()
    email!: string;

    @IsStrongPassword()
    password!: string;

    // is email verified?
    @IsNotEmpty()
    verified!: boolean;

    @IsNotEmpty()
    billingAddress!: string;

    @IsNotEmpty()
    billingCity!: string;

    @IsNotEmpty()
    billingState!: string;

    @IsNotEmpty()
    billingZip!: string;
}
