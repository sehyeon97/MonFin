/*
https://docs.nestjs.com/controllers#controllers
*/

import { Controller, Get, Post } from '@nestjs/common';
import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { MerchantService } from '../services/merchant.service';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { ProductResponse } from '../dto/responses/product.response.dto';

@Controller('merchants')
export class MerchantController {
    constructor(private readonly merchantService: MerchantService) {}
    @Post('register')
    public async registerMerchant(
        merchant: RegisterMerchantRequest,
    ): Promise<void> {
        await this.merchantService.registerNewMerchant(merchant);
    }

    @Post('add-product')
    public async addProduct(req: ProductRequest): Promise<ProductResponse> {
        return await this.merchantService.addProduct(req);
    }

    @Get('view-products')
    public async getMerchantProducts(
        merchantID: string,
    ): Promise<ProductsResponse> {
        return await this.merchantService.getProducts(merchantID);
    }
}
