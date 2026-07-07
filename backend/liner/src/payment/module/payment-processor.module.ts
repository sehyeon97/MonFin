import { PaymentProcessorController } from './../controllers/payment-processor.controller';
import { PaymentProcessorService } from './../services/payment-processor.service';
/*
https://docs.nestjs.com/modules
*/

import { Module } from '@nestjs/common';

@Module({
    imports: [],
    controllers: [PaymentProcessorController],
    providers: [PaymentProcessorService],
})
export class PaymentProcessorModule {}
