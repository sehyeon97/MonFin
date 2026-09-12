export interface BankTransactionRecord {
  transactionID: string;
  lastFour: string;
  merchantName: string; // brand & product name only included in payment processor
  timestamp: string;
  amount: string; // converted to something like $14.99, done by backend
  status: string;
}
