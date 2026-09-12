package com.sehyeon.monfin.bank.repos.transactions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.transactions.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // ordered newest to oldest transactions (gets ALL transactions in history)
    public List<Transaction> findByCardTokenOrderByTimestampDesc(String cardToken);

    // ordered newest to oldest transactions (gets THIS MONTH's transactions only)
    public List<Transaction> findByCardTokenAndTimestampBetweenOrderByTimestampDesc(
        String cardToken, Instant startOfMonth, Instant endOfMonth);

    // Get all transactions for bank account and sort by timestamp in descending order
    // EX: today -> yesterday 8pm -> yesterday 7:30pm
    public List<Transaction> findAllByBankAccountIDOrderByTimestampDesc(UUID bankAccountID);
    
}
