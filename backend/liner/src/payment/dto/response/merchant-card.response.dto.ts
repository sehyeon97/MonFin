import { AddDebitCardResponse } from './debit-card.response.dto';

// embeds debit-card.response.dto.ts
export class MerchantCardResponse {
    // either successful or rejected
    status!: string;
    card!: AddDebitCardResponse;
}
