import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { SaleHistory } from '../entity/merchant-sale.entity';
import { Repository } from 'typeorm';
import { MerchantSale } from '../dto/merchant-sale.dto';

@Injectable()
export class SalesHistoryService {
    constructor(
        @InjectRepository(SaleHistory)
        private readonly salesHistoryRepository: Repository<SaleHistory>,
    ) {}

    public async addApprovedSalesToMerchantHistory(
        merchantSales: MerchantSale[],
    ): Promise<void> {
        const ordersToAdd: SaleHistory[] = this.convertDTOToEntity(
            merchantSales,
            true,
        );
        await this.salesHistoryRepository.insert(ordersToAdd);
    }

    public async addDeclinedSalesToMerchantHistory(
        merchantSales: MerchantSale[],
    ): Promise<void> {
        const ordersToAdd: SaleHistory[] = this.convertDTOToEntity(
            merchantSales,
            false,
        );
        await this.salesHistoryRepository.insert(ordersToAdd);
    }

    private convertDTOToEntity(
        orders: MerchantSale[],
        isApproved: boolean,
    ): SaleHistory[] {
        const ordersToAdd: SaleHistory[] = [];
        for (const order of orders) {
            const orderToAdd: SaleHistory = new SaleHistory();
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
