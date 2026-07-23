import { TypeOrmModule } from '@nestjs/typeorm';
import { CardVault } from '../entity/payment.entity.card.vault';
import { Transaction } from '../entity/payment.entity.transaction';
import { PaymentProcessorController } from './../controllers/payment-processor.controller';
import { PaymentProcessorService } from './../services/payment-processor.service';
/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';
import { RabbitMQModule } from '../../rabbitmq/rabbitmq.module';
import { JWTModule } from '../../auth/jwt.module';

@Module({
    imports: [
        TypeOrmModule.forFeature([CardVault, Transaction]),
        RabbitMQModule,
        JWTModule,
    ],
    controllers: [PaymentProcessorController],
    providers: [PaymentProcessorService],
})
export class PaymentProcessorModule {}
