/*
https://docs.nestjs.com/providers#services
*/

import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Order } from '../entity/customer.entity.order';
import { Repository } from 'typeorm';
import { CustomerOrder } from '../dto/order.dto';

@Injectable()
export class OrderHistoryService {
    constructor(
        @InjectRepository(Order)
        private readonly orderRepository: Repository<Order>,
    ) {}

    public async addApprovedOrder(orders: CustomerOrder[]): Promise<void> {
        // await this.orderRepository.insert(orders);

        const ordersToAdd: Order[] = this.convertDTOToEntity(orders, true);
        await this.orderRepository.insert(ordersToAdd);
    }

    public async addDeclinedOrders(orders: CustomerOrder[]): Promise<void> {
        const ordersToAdd: Order[] = this.convertDTOToEntity(orders, false);
        await this.orderRepository.insert(ordersToAdd);
    }

    private convertDTOToEntity(
        orders: CustomerOrder[],
        isApproved: boolean,
    ): Order[] {
        const ordersToAdd: Order[] = [];
        for (const order of orders) {
            const orderToAdd: Order = new Order();
            orderToAdd.merchantID = order.merchantID;
            orderToAdd.cardToken = order.cardToken;
            orderToAdd.boughtAt = order.timestamp;
            orderToAdd.transactionID = order.transactionID;
            orderToAdd.merchantName = order.merchantName;
            orderToAdd.productName = order.productName;
            orderToAdd.itemCount = 30;
            orderToAdd.totalPrice = order.amount;
            orderToAdd.brand = order.brand;
            orderToAdd.isApproved = isApproved;
            ordersToAdd.push(orderToAdd);
        }
        return ordersToAdd;
    }
}
