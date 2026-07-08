import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class ProductRequest {
    @IsNotEmpty()
    @IsString()
    merchantID!: string;

    @IsNotEmpty()
    @IsString()
    businessName!: string;

    @IsNotEmpty()
    @IsString()
    brand!: string;

    @IsNotEmpty()
    @IsNumber()
    price!: number;

    @IsNotEmpty()
    @IsString()
    desc!: string;

    @IsNotEmpty()
    @IsNumber()
    count!: number;

    @IsNotEmpty()
    @IsString()
    productName!: string;
}
