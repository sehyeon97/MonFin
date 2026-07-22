import { SaleHistory } from '../entity/merchant-sale.entity';
import { Repository } from 'typeorm';
import { MerchantSale } from '../dto/merchant-sale.dto';
export declare class SalesHistoryService {
    private readonly salesHistoryRepository;
    constructor(salesHistoryRepository: Repository<SaleHistory>);
    addApprovedSalesToMerchantHistory(merchantSales: MerchantSale[]): Promise<void>;
    addDeclinedSalesToMerchantHistory(merchantSales: MerchantSale[]): Promise<void>;
    private convertDTOToEntity;
}
