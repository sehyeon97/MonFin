import { IsEmail, IsNotEmpty } from 'class-validator';

export class RegisterMerchantRequest {
    @IsNotEmpty()
    @IsEmail()
    email!: string;

    // @IsStrongPassword()
    @IsNotEmpty() // whitelist: true in validation pipe expects @IsNotEmpty() fields
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
