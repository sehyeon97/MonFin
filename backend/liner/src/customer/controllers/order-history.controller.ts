import { Controller } from '@nestjs/common';
import { OrderHistoryService } from '../services/order-history.service';
import { EventPattern, Payload } from '@nestjs/microservices';
import { CustomerOrder } from '../dto/order.dto';

// rabbitMQ subscribers don't have mappings
// because event updates are what calls them
@Controller()
export class OrderHistoryController {
    constructor(private readonly orderHistoryService: OrderHistoryService) {}

    @EventPattern('payment.approved')
    public async handleApprovedTransactions(
        @Payload() event: CustomerOrder[],
    ): Promise<void> {
        await this.orderHistoryService.addApprovedOrder(event);
    }

    @EventPattern('payment.declined')
    public async handleDeclinedTransactions(
        @Payload() event: CustomerOrder[],
    ): Promise<void> {
        await this.orderHistoryService.addDeclinedOrders(event);
    }
}
