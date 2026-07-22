import { ProductRequest } from './product.request.dto';
export declare class TransactionRequest {
    customerID: string;
    cardToken: string;
    products: ProductRequest[];
}
