import { MerchantService } from '../services/merchant.service';
import { MerchantController } from '../controllers/merchant.controller';
/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Merchant } from '../entity/merchant.entity';
import { Product } from '../entity/merchant.product.entity';

@Module({
    imports: [TypeOrmModule.forFeature([Merchant, Product])],
    controllers: [MerchantController],
    providers: [MerchantService],
})
export class MerchantModule {}
