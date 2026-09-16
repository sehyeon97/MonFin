import {
    IsNotEmpty,
    IsNumber,
    IsString,
    MaxLength,
    Min,
} from 'class-validator';

export class UpdateProductRequest {
    @IsNotEmpty()
    @IsString()
    businessName!: string;

    @IsNotEmpty()
    @IsString()
    brand!: string;

    @IsNumber()
    @IsNotEmpty()
    price!: number;

    @IsNotEmpty()
    @MaxLength(250)
    @IsString()
    desc!: string;

    @IsNotEmpty()
    @Min(1)
    @IsNumber()
    count!: number;

    @IsNumber()
    @IsNotEmpty()
    newPrice!: number;

    @IsNotEmpty()
    @MaxLength(250)
    @IsString()
    newDesc!: string;

    @IsNotEmpty()
    @Min(1)
    @IsNumber()
    newCount!: number;
}
