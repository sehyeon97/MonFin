package com.sehyeon.monfin.transaction.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sehyeon.monfin.bank.domainobjs.FraudDetectionResult;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.transactions.Transaction;
import com.sehyeon.monfin.bank.services.transactions.FraudDetectionService;

/**
 * Because this class does not depend on any dependencies,
 * it does not need Mockito
 * A simple JUnit testing suffices
 */
public class FraudDetectionServiceTest {

    private FraudDetectionService service;

    private List<Transaction> transactions;
    private Transaction transaction;
    private Card card;

    private static final int CARD_BALANCE = 100000;
    private static final int CARD_DAILY_LIMIT = 10000;
    private static final int CARD_MONTHLY_LIMIT = 100000;

    // constants for fraud detection messages
    private static final String SUCCESSIVE_TRANSACTION = "Successive transaction.";
    private static final String ABOVE_AVERAGE_MONTHLY_SPENDING = "Transaction amount is above average spending.";
    private static final String THREE_DIFF_MERCHANTS_IN_A_MINUTE = "Card used in multiple places successively.";
    private static final String THREE_OR_MORE_REPEATED_DECLINES = "Card has been declined three times previously";
    private static final String ABOVE_AVERAGE_DAILY_SPENDING = "Amount is suspiciously high compared to average daily spending.";
    // private static final String EIGHTY_PERCENT_MONTHLY_LIMIT_DAY_ONE = "Spent almost the monthly limit on day 1 of the month.";

    @BeforeEach
    public void init() {
        service = new FraudDetectionService();
        transactions = populateMonthlyTransactions();
        transaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Amazon", UUID.randomUUID(), 1000, Instant.now(), "Pending");
        BasicCardInfo info = new BasicCardInfo(
            "1234123412341234", "12", "2026", "Yo To", "123");
        card = new Card(CARD_BALANCE, 0, info,
            CardType.DEBIT, CardNetwork.VISA, CardTier.GOLD, Instant.now(), CARD_DAILY_LIMIT, CARD_MONTHLY_LIMIT);
        card.setCardStatus(CardStatus.ACTIVE);
    }

    // 2+ transactions within the last minute
    @Test
    public void shouldDetectSuccessiveTransaction() {
        // Arrange
        Transaction successiveTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Costco", UUID.randomUUID(), 100, Instant.now(), "");
        transactions.add(0, transaction);

        // Act
        FraudDetectionResult result = service.evaluate(transactions, successiveTransaction, card);

        // Assert
        assertThat(transactions.size() == 29);
        assertTrue(result.detectedFrauds().contains(SUCCESSIVE_TRANSACTION));
        assertTrue(result.riskScore() >= 25);
    }

    //  Amount higher than user's average expense, which is calculated with at least 10 previous transactions
    @Test
    public void shouldDetectDailyHighAmountOutlier() {
        // Arrange
        Transaction highAmountTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Costco", UUID.randomUUID(), CARD_DAILY_LIMIT - 100, Instant.now(), "");
        transactions.add(0, transaction);

        // Act
        FraudDetectionResult result = service.evaluate(transactions, highAmountTransaction, card);

        // Assert
        assertTrue(result.detectedFrauds().contains(ABOVE_AVERAGE_MONTHLY_SPENDING));
    }

    // 3+ transactions with different merchants within a minute
    @Test // hmm this method conflicts with the first algorithm
    public void shouldDetectThreePlusSuccessiveMerchantTransactions() {
        // Arrange
        Transaction threePlusTransactionsDiffMerchants = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Amazon", UUID.randomUUID(), 100, Instant.now(), "");

        // Act
        for (int i = 0; i < 2; i++) {
            UUID merchantID = UUID.randomUUID();
            transactions.add(0, new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            i == 0 ? "Costco" : "BestBuy", merchantID, 100, Instant.now(), "Approved"));
        }
        FraudDetectionResult result = service.evaluate(transactions, threePlusTransactionsDiffMerchants, card);

        // Assert
        assertTrue(result.detectedFrauds().contains(THREE_DIFF_MERCHANTS_IN_A_MINUTE));
    }

    // 3+ repeated declines
    @Test
    public void shouldDetectRepeatedDeclines() {
        // Arrange
        Transaction declinedTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Costco", UUID.randomUUID(), 100, Instant.now(), "Declined");

        // Act
        for (int i = 0; i < 3; i++) {
            transactions.add(declinedTransaction);
        }
        FraudDetectionResult result = service.evaluate(transactions, transaction, card);

        // Assert
        assertTrue(result.detectedFrauds().contains(THREE_OR_MORE_REPEATED_DECLINES));
    }

    // Transaction amount is a spike (during a day)
    @Test
    public void shouldDetectDailySpike() {
        // Arrange
        Transaction spikedTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "29", "6", "2026",
            "Costco", UUID.randomUUID(), CARD_DAILY_LIMIT, Instant.now(), "");

        // Act
        FraudDetectionResult result = service.evaluate(transactions, spikedTransaction, card);

        // Assert
        assertTrue(result.detectedFrauds().contains(ABOVE_AVERAGE_DAILY_SPENDING));
    }

    // happy path with zero to low risk score
    @Test
    public void shouldPassFraudDetection() {
        // Arrange
        Instant today = Instant.now();
        Instant yesterday = today.minus(1, ChronoUnit.DAYS);
        Instant dayBeforeYesterday = yesterday.minus(1, ChronoUnit.DAYS);
        Transaction firstSafeTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "26", "6", "2026",
            "Costco", UUID.randomUUID(), 100, today, "Pending");
        Transaction secondSafeTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "27", "6", "2026",
            "Costco", UUID.randomUUID(), 100, yesterday, "Pending");
        Transaction thirdSafeTransaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), "28", "6", "2026",
            "Costco", UUID.randomUUID(), 100, dayBeforeYesterday, "Pending");

        // Act
        transactions.add(0, firstSafeTransaction);
        transactions.add(0, secondSafeTransaction);
        transactions.add(0, thirdSafeTransaction);
        FraudDetectionResult result = service.evaluate(transactions, transaction, card);

        // Assert
        assertTrue(result.detectedFrauds().isEmpty());
        assertTrue(result.riskScore() < 1);
        assertTrue(result.passed());
    }

    private List<Transaction> populateMonthlyTransactions() {
        List<Transaction> monthlyTransactions = new ArrayList<>();
        Instant today = Instant.now();
        for (int i = 0; i < 28; i++) {
            int day = 28 - i;
            int factor = 0;
            Transaction transaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID().toString(), Integer.toString(day), "6", "2026",
            "Merchant",  UUID.randomUUID(), 2000, today.minus(i + 1, ChronoUnit.DAYS),
            factor % 2 == 0 ? "Approved" : "Declined");
            monthlyTransactions.add(transaction);
            factor += 1;
        }
        return monthlyTransactions;
    }
    
}
