import { CustomerSavedPaymentMethodResponse } from '../dto/response/customer-payment-method.response.dto';
import { AddPaymentMethodRequest } from '../dto/request/customer-payment-method.request.dto';
import { PaymentProcessorService } from '../services/payment-processor.service';
import { AddMerchantDebitCardRequest } from '../dto/request/merchant-card.request.dto';
import { MerchantCardResponse } from '../dto/response/merchant-card.response.dto';
import { BankOTPTransactionResult } from '../dto/request/bank-otp-result.request.dto';
import { TransactionRequest } from '../dto/request/transaction.request.dto';
import { TransactionResponse } from '../dto/response/transaction.response.dto';
export declare class PaymentProcessorController {
    private readonly paymentProcessorService;
    constructor(paymentProcessorService: PaymentProcessorService);
    addDebitForMerchant(req: AddMerchantDebitCardRequest): Promise<MerchantCardResponse>;
    viewPaymentMethods(customerID: string): Promise<CustomerSavedPaymentMethodResponse[]>;
    addPaymentMethod(paymentMethod: AddPaymentMethodRequest): Promise<void>;
    createTransaction(req: TransactionRequest): Promise<TransactionResponse>;
    saveTransactionResults(transactionResults: BankOTPTransactionResult[]): Promise<void>;
}
