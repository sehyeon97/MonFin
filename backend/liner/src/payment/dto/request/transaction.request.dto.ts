import { IsArray, IsNotEmpty, IsString } from 'class-validator';
import { ProductRequest } from './product.request.dto';

/**
 * Request by Customer on their checkout
 * customerID: who is making this request / who owns checkout
 * cardToken: customer's selected card
 * products: the list of products in their cart
 */
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
