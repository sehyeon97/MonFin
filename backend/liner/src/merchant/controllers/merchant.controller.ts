/*
https://docs.nestjs.com/controllers#controllers
*/

import {
    Body,
    Controller,
    Get,
    Post,
    Query,
    Req,
    UseGuards,
} from '@nestjs/common';
import { RegisterMerchantRequest } from '../dto/requests/register-merchant-request.dto';
import { MerchantService } from '../services/merchant.service';
import { ProductsResponse } from '../dto/responses/list.products.response.dto';
import { ProductRequest } from '../dto/requests/product.request.dto';
import { ProductResponse } from '../dto/responses/product.response.dto';
import { Merchant } from '../entity/merchant.entity';
import { JwtAuthGuard } from '../../auth/jwt-auth.guard';
import * as jwtAuthGuardDto from '../../auth/jwt-auth-guard.dto';
import { UpdateProductRequest } from '../dto/requests/update-product-request.dto';

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

    @UseGuards(JwtAuthGuard)
    @Post('add-product')
    public async addProduct(
        @Req() req: jwtAuthGuardDto.AuthenticatedRequest,
        @Body() productReq: ProductRequest,
    ): Promise<ProductResponse> {
        return await this.merchantService.addProduct(productReq, req.user);
    }

    // Merchant viewing their own product
    @UseGuards(JwtAuthGuard)
    @Get('view-products')
    public async getMerchantProducts(
        @Req() req: jwtAuthGuardDto.AuthenticatedRequest,
    ): Promise<ProductsResponse> {
        console.log('Getting merchant products for preview...');
        const products = await this.merchantService.getProductsForMerchant(
            req.user.id,
        );
        console.log(`number of products: ${products.products.length}`);
        return products;
    }

    @UseGuards(JwtAuthGuard)
    @Post('update-product')
    public async updateProduct(
        @Req() req: jwtAuthGuardDto.AuthenticatedRequest,
        @Body() updateRequest: UpdateProductRequest,
    ): Promise<ProductResponse> {
        return await this.merchantService.updateProduct(
            updateRequest,
            req.user,
        );
    }

    // Customer viewing Merchant products
    @Get('products')
    public async getAllMerchantsAndTheirProductsForCustomer(
        @Query('businessName') businessName: string,
    ): Promise<ProductsResponse> {
        return await this.merchantService.getProductsForCustomer(businessName);
    }
}
