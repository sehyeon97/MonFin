import { IsArray, IsNotEmpty, IsString } from 'class-validator';
import { ProductRequest } from './product.request.dto';

export class TransactionRequest {
    @IsNotEmpty()
    @IsString()
    customerID!: string;

    @IsNotEmpty()
    @IsString()
    cardToken!: string;

    @IsNotEmpty()
    @IsArray()
    products!: ProductRequest[];
}
