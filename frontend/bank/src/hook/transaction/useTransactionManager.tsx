import { BankTransactionRecord } from "@/dtos/transaction-history/bank-transaction-history.response";
import { getTransactions } from "@/service/handler/transactionHandler";
import { useEffect, useState } from "react";

export function useTransactionManager() {
    const [transactions, setTransactions] = useState<BankTransactionRecord[]>([]);

    useEffect(() => {
        const loadTransactions = async () => {
            const data: BankTransactionRecord[] = await getTransactions();
            setTransactions(data);
        }

        loadTransactions();
    }, []);

    // Later, could return object { transactions, addTransaction }
    // when transactions are made directly via the bank account in the bank portal
    return transactions;
}