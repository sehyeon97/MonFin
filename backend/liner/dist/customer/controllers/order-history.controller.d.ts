import { OrderHistoryService } from '../services/order-history.service';
import { CustomerOrder } from '../dto/order.dto';
export declare class OrderHistoryController {
    private readonly orderHistoryService;
    constructor(orderHistoryService: OrderHistoryService);
    handleApprovedTransactions(event: CustomerOrder[]): Promise<void>;
    handleDeclinedTransactions(event: CustomerOrder[]): Promise<void>;
}
