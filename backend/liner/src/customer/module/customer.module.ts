import { CustomerController } from '../controllers/customer.controller';
/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Customer } from '../entity/customer.entity.user';
import { CustomerAccountService } from '../services/customer-account.service';
import { CardVault } from '../../payment/entity/payment.entity.card.vault';
import { Order } from '../entity/customer.entity.order';

@Module({
    imports: [TypeOrmModule.forFeature([Customer, CardVault, Order])],
    controllers: [CustomerController],
    providers: [CustomerAccountService],
})
export class CustomerModule {}
