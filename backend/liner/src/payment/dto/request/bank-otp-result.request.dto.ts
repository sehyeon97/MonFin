import { IsNotEmpty } from 'class-validator';
import { TransactionData } from '../transaction-data.dto';
import { BankResponseData } from '../bank-response-data.dto';

// This needs to match TransactionResponse.java
export class BankOTPTransactionResult {
    @IsNotEmpty()
    transactionData!: TransactionData;

    bankResData!: BankResponseData;
}
