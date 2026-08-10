export class TransactionData {
  transactionID!: string;
  customerID!: string;
  cardToken!: string;
  merchantID!: string;
  merchantName!: string; // aka business name
  brand!: string;
  productName!: string;
  timestamp!: string;
  amount!: number;
}
