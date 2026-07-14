import { Controller } from '@nestjs/common';
import { SalesHistoryService } from '../services/sales-history.service';
import { EventPattern, Payload } from '@nestjs/microservices';
import { MerchantSale } from '../dto/merchant-sale.dto';

@Controller()
export class SalesHistoryController {
    constructor(private readonly salesHistory: SalesHistoryService) {}

    @EventPattern('payment.approved')
    public async handleApprovedTransactions(
        @Payload() event: MerchantSale[],
    ): Promise<void> {
        await this.salesHistory.addApprovedSalesToMerchantHistory(event);
    }

    @EventPattern('payment.declined')
    public async handleDeclinedTransactions(
        @Payload() event: MerchantSale[],
    ): Promise<void> {
        await this.salesHistory.addDeclinedSalesToMerchantHistory(event);
    }
}
