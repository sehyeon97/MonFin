import { Merchant } from '../entity/merchant.entity';
import { Repository } from 'typeorm';
import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { Product } from '../entity/merchant.product.entity';
import { ProductResponse } from '../dto/responses/product.response.dto';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { SignInMerchantRequest } from '../dto/requests/sign-in-merchant.request.dto';
import { JWTAccessPayload } from '../../auth/jwt-access-payload.dto';
import { UpdateProductRequest } from '../dto/requests/update-product-request.dto';
export declare class MerchantService {
    private readonly merchantRepository;
    private readonly productRepository;
    constructor(merchantRepository: Repository<Merchant>, productRepository: Repository<Product>);
    registerNewMerchant(req: RegisterMerchantRequest): Promise<Merchant>;
    signIn(req: SignInMerchantRequest): Promise<string>;
    addProduct(req: ProductRequest, user: JWTAccessPayload): Promise<ProductResponse>;
    updateProduct(req: UpdateProductRequest, user: JWTAccessPayload): Promise<ProductResponse>;
    getProductsForMerchant(merchantID: string): Promise<ProductsResponse>;
    getProductsForCustomer(businessName: string): Promise<ProductsResponse>;
    reduceProductCount(merchantID: string, productID: string, num: number): Promise<void>;
}
