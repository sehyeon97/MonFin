import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { MerchantService } from '../services/merchant.service';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { ProductResponse } from '../dto/responses/product.response.dto';
import * as jwtAuthGuardDto from '../../auth/jwt-auth-guard.dto';
import { UpdateProductRequest } from '../dto/requests/update-product-request.dto';
export declare class MerchantController {
    private readonly merchantService;
    constructor(merchantService: MerchantService);
    registerMerchant(merchant: RegisterMerchantRequest): Promise<string>;
    addProduct(req: jwtAuthGuardDto.AuthenticatedRequest, productReq: ProductRequest): Promise<ProductResponse>;
    getMerchantProducts(req: jwtAuthGuardDto.AuthenticatedRequest): Promise<ProductsResponse>;
    updateProduct(req: jwtAuthGuardDto.AuthenticatedRequest, updateRequest: UpdateProductRequest): Promise<ProductResponse>;
    getAllMerchantsAndTheirProductsForCustomer(businessName: string): Promise<ProductsResponse>;
}
