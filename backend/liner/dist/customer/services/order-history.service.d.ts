import { Order } from '../entity/customer.entity.order';
import { Repository } from 'typeorm';
import { CustomerOrder } from '../dto/order.dto';
import { PurchasedItem } from '../dto/responses/purchased-item.response.dto';
export declare class OrderHistoryService {
    private readonly orderRepository;
    constructor(orderRepository: Repository<Order>);
    addApprovedOrder(orders: CustomerOrder[]): Promise<void>;
    addDeclinedOrders(orders: CustomerOrder[]): Promise<void>;
    getCustomerOrderHistory(customerID: string): Promise<PurchasedItem[]>;
    private convertDTOToEntity;
}
