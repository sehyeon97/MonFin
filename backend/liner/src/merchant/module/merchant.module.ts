import { MerchantService } from '../services/merchant.service';
import { MerchantController } from '../controllers/merchant.controller';
/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Merchant } from '../entity/merchant.entity';
import { Product } from '../entity/merchant.product.entity';
import { CardVault } from '../../payment/entity/payment.entity.card.vault';

@Module({
    imports: [TypeOrmModule.forFeature([Merchant, Product, CardVault])],
    controllers: [MerchantController],
    providers: [MerchantService],
})
export class MerchantModule {}
