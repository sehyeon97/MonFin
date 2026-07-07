import { IsNotEmpty, IsNumber, IsUUID, Max, Min } from 'class-validator';

export class ProductRequest {
    @IsUUID()
    @IsNotEmpty()
    merchantID!: string;

    @IsNotEmpty()
    businessName!: string;

    @IsNotEmpty()
    brand!: string;

    @IsNumber()
    @IsNotEmpty()
    price!: number;

    @IsNotEmpty()
    @Max(200)
    desc!: string;

    @IsNotEmpty()
    @Min(1)
    count!: number;
}
