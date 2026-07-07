import { ProductResponse } from './product.response.dto';

export class ProductsResponse {
    merchantID!: string;
    products!: ProductResponse[];
}
