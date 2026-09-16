/*
https://docs.nestjs.com/providers#services
*/

import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
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

@Injectable()
export class MerchantService {
    constructor(
        @InjectRepository(Merchant)
        private readonly merchantRepository: Repository<Merchant>,
        @InjectRepository(Product)
        private readonly productRepository: Repository<Product>,
    ) {}

    public async registerNewMerchant(
        req: RegisterMerchantRequest,
    ): Promise<Merchant> {
        const merchant: Merchant = this.merchantRepository.create();
        merchant.email = req.email;
        merchant.password = req.password;
        merchant.verified = req.verified;
        merchant.billingAddress = req.billingAddress;
        merchant.billingCity = req.billingCity;
        merchant.billingState = req.billingState;
        merchant.billingZip = req.billingZip;
        return await this.merchantRepository.save(merchant);
    }

    public async signIn(req: SignInMerchantRequest): Promise<string> {
        const merchant: Merchant | null = await this.merchantRepository.findOne(
            {
                where: { email: req.email, password: req.password },
            },
        );

        return merchant ? merchant.id : '';
    }

    public async addProduct(
        req: ProductRequest,
        user: JWTAccessPayload,
    ): Promise<ProductResponse> {
        const product: Product = this.productRepository.create({
            merchantID: user.id,
            businessName: req.businessName,
            brand: req.brand,
            price: req.price,
            desc: req.desc,
            count: req.count,
        });
        await this.productRepository.save(product);
        return {
            businessName: product.businessName,
            brand: product.brand,
            price: product.price,
            desc: product.desc,
            count: product.count,
        };
    }

    public async updateProduct(
        req: UpdateProductRequest,
        user: JWTAccessPayload,
    ): Promise<ProductResponse> {
        await this.productRepository.update(
            {
                merchantID: user.id,
                businessName: req.businessName,
                brand: req.brand,
                price: req.price,
                desc: req.desc,
                count: req.count,
            },
            {
                price: req.newPrice,
                desc: req.newDesc,
                count: req.newCount,
            },
        );

        const product: Product | null = await this.productRepository.findOne({
            where: {
                merchantID: user.id,
                businessName: req.businessName,
                brand: req.brand,
                price: req.newPrice,
                desc: req.newDesc,
                count: req.newCount,
            },
        });

        return product ? product : new ProductResponse();
    }

    public async getProductsForMerchant(
        merchantID: string,
    ): Promise<ProductsResponse> {
        const products: Product[] = await this.productRepository.find({
            where: { merchantID: merchantID },
        });

        console.log(`number of products in database: ${products.length}`);

        const merchantProducts: ProductResponse[] = products.map(
            (product: Product) => {
                const res: ProductResponse = new ProductResponse();
                res.businessName = product.businessName;
                res.brand = product.brand;
                res.price = product.price;
                res.desc = product.desc;
                res.count = product.count;
                return res;
            },
        );

        console.log(
            `number of products in response: ${merchantProducts.length}`,
        );

        return {
            products: merchantProducts,
        };
    }

    public async getProductsForCustomer(
        businessName: string,
    ): Promise<ProductsResponse> {
        const products = await this.productRepository.find({
            where: { businessName: businessName },
        });

        const merchantProducts: ProductResponse[] = products.map(
            (product: Product) => {
                const res: ProductResponse = new ProductResponse();
                res.businessName = product.businessName;
                res.brand = product.brand;
                res.price = product.price;
                res.desc = product.desc;
                res.count = product.count;
                return res;
            },
        );

        return {
            products: merchantProducts,
        };
    }

    // called by payment processor
    public async reduceProductCount(
        merchantID: string,
        productID: string,
        num: number,
    ): Promise<void> {
        const product: Product | null = await this.productRepository.findOne({
            where: { id: productID, merchantID: merchantID },
        });

        if (product) {
            product.count -= num;
            await this.productRepository.save(product);
        }
    }
}
