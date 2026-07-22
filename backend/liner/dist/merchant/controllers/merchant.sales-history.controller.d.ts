import { SalesHistoryService } from '../services/sales-history.service';
import { MerchantSale } from '../dto/merchant-sale.dto';
export declare class SalesHistoryController {
    private readonly salesHistory;
    constructor(salesHistory: SalesHistoryService);
    handleApprovedTransactions(event: MerchantSale[]): Promise<void>;
    handleDeclinedTransactions(event: MerchantSale[]): Promise<void>;
}
