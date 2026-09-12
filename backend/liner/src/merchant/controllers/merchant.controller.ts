/*
https://docs.nestjs.com/controllers#controllers
*/

import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { MerchantService } from '../services/merchant.service';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { ProductResponse } from '../dto/responses/product.response.dto';
import { Merchant } from '../entity/merchant.entity';

@Controller('payment-api/merchants')
export class MerchantController {
    constructor(private readonly merchantService: MerchantService) {}
    @Post('register')
    public async registerMerchant(
        @Body() merchant: RegisterMerchantRequest,
    ): Promise<string> {
        console.log('MERCHANT REQUEST:', merchant);
        console.log('MERCHANT PASSWORD:', merchant?.password);
        const result: Merchant =
            await this.merchantService.registerNewMerchant(merchant);
        return result.getID();
    }

    // *** REFACTORED TO JWT AUTH *** Testing in progress. . . then delete after success
    // @Post('login')
    // public async loginMerchant(
    //     @Body() request: SignInMerchantRequest,
    // ): Promise<string> {
    //     return await this.merchantService.signIn(request);
    // }

    @Post('add-product')
    public async addProduct(
        @Body() req: ProductRequest,
    ): Promise<ProductResponse> {
        return await this.merchantService.addProduct(req);
    }

    @Get('view-products')
    public async getMerchantProducts(
        @Query('merchantID') merchantID: string,
    ): Promise<ProductsResponse> {
        return await this.merchantService.getProducts(merchantID);
    }
}
