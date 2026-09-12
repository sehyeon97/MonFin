import { BankTransactionRecord } from "@/dtos/transaction-history/bank-transaction-history.response";
import { useTransactionManager } from "@/hook/transaction/useTransactionManager";
import { Content } from "../Content";
import { TransactionFilter } from "./TransactionFilter";
import { Container } from "@/component/Container";
import { Row } from "../row/Row";
import { TRANSACTION_COL_CLASS_NAME, TRANSACTION_ROW_CLASS_NAME } from "@/style/classnames";
import { formatInstant } from "@/util/format-instant";

// TRANSACTION_ROW_CLASS_NAME & TRANSACTION_COL_CLASS_NAME not set
// Will give it styling after core payment processor frontend implemented
export function TransactionContent() {
    const transactions: BankTransactionRecord[] = useTransactionManager();

    // Custom Filter on left hand side as Container -> Column
    // Transactions in the mid-left ish to mid-right ish
    return (
        <Content className="space-x-40">
            <TransactionFilter />
            <Container title="Transaction History">
                <Row className={TRANSACTION_ROW_CLASS_NAME}>
                    <p className={TRANSACTION_COL_CLASS_NAME}>Merchant Name</p>
                    <p className={TRANSACTION_COL_CLASS_NAME}>Amount</p>
                    <p className={TRANSACTION_COL_CLASS_NAME}>Paid With</p>
                    <p className={TRANSACTION_COL_CLASS_NAME}>Timestamp</p>
                    <p className={TRANSACTION_COL_CLASS_NAME}>Status</p>
                </Row>
                {transactions.map((transaction) => (
                    <Row key={transaction.transactionID} className={TRANSACTION_ROW_CLASS_NAME}>
                        <p className={TRANSACTION_COL_CLASS_NAME}>{transaction.merchantName}</p>
                        <p className={TRANSACTION_COL_CLASS_NAME}>{transaction.amount}</p>
                        <p className={TRANSACTION_COL_CLASS_NAME}>{transaction.lastFour}</p>
                        <p className={TRANSACTION_COL_CLASS_NAME}>{formatInstant(transaction.timestamp)}</p>
                        <p className={TRANSACTION_COL_CLASS_NAME}>{transaction.status}</p>
                    </Row>
                ))}
            </Container>
        </Content>
    );
}