import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SaleHistory } from '../entity/merchant-sale.entity';
import { RabbitMQModule } from '../../rabbitmq/rabbitmq.module';
import { SalesHistoryService } from '../services/sales-history.service';
import { SalesHistoryController } from '../controllers/merchant.sales-history.controller';

@Module({
    imports: [TypeOrmModule.forFeature([SaleHistory]), RabbitMQModule],
    controllers: [SalesHistoryController],
    providers: [SalesHistoryService],
})
export class MerchantSalesModule {}
