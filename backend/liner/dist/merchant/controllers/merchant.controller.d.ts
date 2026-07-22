import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { MerchantService } from '../services/merchant.service';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { ProductResponse } from '../dto/responses/product.response.dto';
import { SignInMerchantRequest } from '../dto/requests/sign-in-merchant.request.dto';
export declare class MerchantController {
    private readonly merchantService;
    constructor(merchantService: MerchantService);
    registerMerchant(merchant: RegisterMerchantRequest): Promise<void>;
    loginMerchant(request: SignInMerchantRequest): Promise<string>;
    addProduct(req: ProductRequest): Promise<ProductResponse>;
    getMerchantProducts(merchantID: string): Promise<ProductsResponse>;
}
