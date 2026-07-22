import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { OrderHistoryService } from '../services/order-history.service';
import { OrderHistoryController } from '../controllers/order-history.controller';
import { Order } from '../entity/customer.entity.order';
import { RabbitMQModule } from '../../rabbitmq/rabbitmq.module';

@Module({
    imports: [TypeOrmModule.forFeature([Order]), RabbitMQModule],
    controllers: [OrderHistoryController],
    providers: [OrderHistoryService],
})
export class CustomerOrderHistoryModule {}
