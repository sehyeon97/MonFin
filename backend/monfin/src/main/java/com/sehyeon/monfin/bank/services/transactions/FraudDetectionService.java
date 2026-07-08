package com.sehyeon.monfin.bank.services.transactions;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.domainobjs.FraudDetectionResult;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.transactions.Transaction;

/**
 *  | FRAUD RULES (passing them will increase the riskScore which may freeze a card) |
 * A. Too many transactions (2+) within the last minute (utilize timestamp and db records)
 * B. Amount is higher than user's average expense
 * C. Same card used in 3+ different merchants
 * D. Geographically impossible to make purchases in two distant places (part of mobile order so out of web app scope)
 * E. 3+ repeated declines
 * F. Spending spike for the day compared to average daily spending
 * G. First day of month and spent 80% of monthly spending limit (REMOVED because daily limit < monthly limit. would never be true)
 * H. If transaction exceeds x amount or for part G, and text confirmation was not confirmed, it could be fraud
 * Part H. confirmation text will be done separately in otpService.java
 * Reason: when frontend receives "Fraud detected", it will automatically send the customer
 * to the OTP page for them to enter the passcode. This server will need to send a OTP passcode 
 * to the phone number on file with the associated bank account
 */
@Service // doesn't need to be service, but easier to make test cases + possible future scalability
public class FraudDetectionService {

    // constants
    private static final ZoneId zone = ZoneId.of("America/Los_Angeles"); // PST
    private static final double HIGHER_THAN_AVERAGE_FACTOR = 1.5; // for average monthly spending
    // private static final double GEOGRAPHICAL_DISTANCE_FACTOR = 50; // in miles
    private static final double DAILY_SPIKE_FACTOR = 4; // for average daily spending
    // private static final double MONTHLY_LIMIT_FACTOR = 0.8; // day 1 spending cannot exceed 80% of monthly limit || REMOVED ||

    public FraudDetectionService() {}

    /**
     * Accepts a list of monthly transactions, the current transaction request, and the card used for the transaction
     * The returned riskScore does not take into account the card's current riskScore
     * Therefore, the returned value must be added to the card's riskScore
     */
    public FraudDetectionResult evaluate(List<Transaction> transactions, Transaction transaction, Card card) {
        // Define variables
        int riskScore = 0;
        List<String> detectedFrauds = new ArrayList<>();
        boolean passed = true;
        ZonedDateTime transactionTimestamp = convertInstantToZonedDateTime(transaction.getTimestamp());

        // A. Too many (2+) transactions within the last minute
        // transactions is listed in descending order from newest -> oldest
        if (transactions.size() >= 1) {
            ZonedDateTime mostRecentTransactionTimestamp = convertInstantToZonedDateTime(transactions.get(0).getTimestamp());
            if (isTransactionSuccessive(transactionTimestamp, mostRecentTransactionTimestamp)) {
                riskScore += 25;
                detectedFrauds.add("Successive transaction.");
            }
        }

        // B. Amount is higher than user's average expense taken by at least 10 previous transactions
        if (transactions.size() >= 10 && isAboveAverageExpense(transactions, transaction)) {
            riskScore += 20;
            detectedFrauds.add("Transaction amount is above average spending.");
        }

        // C. Same (customer) card used by 3 different merchants within a minute
        if (transactions.size() >= 2 
            && isUsedByThreeMerchants(transactions.get(0), transactions.get(1), transaction)) {
                riskScore += 50;
                detectedFrauds.add("Card used in multiple places successively.");
        }

        // D. Skip for now

        // E. 3+ repeated declines
        int riskFactor = computeDeclines(transactions);
        if (riskFactor > 0) {
            riskScore += riskFactor;
            detectedFrauds.add("Card has been declined three times previously");
        }

        // F. Unexpected high transaction amount during the day using DAILY_SPIKE_FACTOR
        if (isASpike(transactions, transaction)) {
            riskScore += 20;
            detectedFrauds.add("Amount is suspiciously high compared to average daily spending.");
        }

        // G. First day of month and spent 80% of monthly spending limit ||| * REMOVED * |||
        // if (isReachingMonthlyLimitAtDayOne(card.getMonthlyLimit(), transaction)) {
        //     riskScore += 5;
        //     detectedFrauds.add("Spent almost the monthly limit on day 1 of the month.");
        // }

        if (riskScore >= 71) {
            passed = false;
        }
        // passed everything
        return new FraudDetectionResult(riskScore, detectedFrauds, passed);
    }

    private ZonedDateTime convertInstantToZonedDateTime(Instant timestamp) {
        return ZonedDateTime.ofInstant(timestamp, zone);
    }

    // All transaction timestamps will share the same month and year
    // Therefore, only need to compare the day, and if the same day, compare hours and minutes
    private boolean isTransactionSuccessive(ZonedDateTime curr, ZonedDateTime prev) {
        // current transaction is at least 1+ days after previous transaction
        if (curr.getDayOfMonth() > prev.getDayOfMonth()) { // 1 - 31
            return false;
        }

        // transaction is on the same day
        // current transaction is at least 1+ hours after previous transaction
        if (curr.getHour() > prev.getHour()) { // 0 - 23
            return false;
        }

        // edge case: current transaction is e.g. 2:00 but previous is 1:59
        if (curr.getMinute() == 0 && prev.getMinute() == 59) { // 0 - 59
            return true;
        }

        // transaction is on the same hour of the same day
        // transaction occurred within 1 minute successively
        if (curr.getMinute() - prev.getMinute() <= 1) {
            return true;
        }

        return false;
    }

    // Current transaction amount is 50% higher than average expense
    // 10 = $0.10 | 100 = $1 | 1000 = $10 | 10,000 = $100 | 100,000 = $1000 | 1,000,000 = $10,000
    private boolean isAboveAverageExpense(List<Transaction> transactions, Transaction curr) {
        int currVal = Integer.parseInt(curr.getAmount());
        int sum = 0;
        for (Transaction transaction : transactions) {
            int amount = Integer.parseInt(transaction.getAmount());
            sum += amount;
        }

        int average = Math.round(sum / transactions.size());
        return currVal >= average * HIGHER_THAN_AVERAGE_FACTOR;
    }

    // used by 3 or more merchants successively
    private boolean isUsedByThreeMerchants(Transaction prev1, Transaction prev2, Transaction curr) {
        return isTransactionSuccessive(convertInstantToZonedDateTime(curr.getTimestamp()), convertInstantToZonedDateTime(prev1.getTimestamp())) 
            && isTransactionSuccessive(convertInstantToZonedDateTime(prev1.getTimestamp()), convertInstantToZonedDateTime(prev2.getTimestamp()))
            && curr.getMerchantID() != prev1.getMerchantID()
            && prev1.getMerchantID() != prev2.getMerchantID();
    }

    // has been declined 3 times previously
    // current transaction is not included because
    // if it reached the fraud detection service,
    // it means the transaction was about to finalize
    // this means that this transaction would have been approved
    // however, this method checks that the past 3 transactions were not declined
    private int computeDeclines(List<Transaction> transactions) {
        int riskScore = 0;
        for (int i = 2; i < transactions.size(); i++) {
            String p3 = transactions.get(i).getResult();
            String p2 = transactions.get(i - 1).getResult();
            String p1 = transactions.get(i - 2).getResult();

            if (p3.equalsIgnoreCase(p2) && p2.equalsIgnoreCase(p1) && p1.equalsIgnoreCase("declined")) {
                riskScore += 5;
            }
        }
        return riskScore;
    }

    // Accepts current transaction
    // Computes average daily spending for each day of the month
    // returns true if this transaction amount is higher than the HIGHEST average daily spending by factor
    private boolean isASpike(List<Transaction> transactions, Transaction transaction) {
        int amount = Integer.parseInt(transaction.getAmount());

        // at most 31 days in a month
        int maxAvgDaily = 0;
        int dailyExpenseCount = 0;
        int dailyExpenseSum = 0;

        // remember that transactions are ordered newest to oldest
        // and that the timeframe of the transactions is set to current month
        int previousDay = 31;

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            ZonedDateTime tTime = convertInstantToZonedDateTime(t.getTimestamp());
            int tDay = tTime.getDayOfMonth();

            // reset if day changed
            if (tDay < previousDay) {
                if (dailyExpenseCount > 0) {
                    int avgDaily = Math.round(dailyExpenseSum / dailyExpenseCount);
                    maxAvgDaily = Math.max(maxAvgDaily, avgDaily);
                }
                previousDay = tDay;
                dailyExpenseCount = 0;
                dailyExpenseSum = 0;
            }

            dailyExpenseCount += 1;
            dailyExpenseSum += Integer.parseInt(t.getAmount());
        }

        return amount > maxAvgDaily * DAILY_SPIKE_FACTOR;
    }

    // first day of month and spent 80% of monthly spending limit ||| REMOVED |||
    // private boolean isReachingMonthlyLimitAtDayOne(int monthlyLimit, Transaction transaction) {
    //     if (convertInstantToZonedDateTime(transaction.getTimestamp()).getDayOfMonth() <= 1) {
    //         return Integer.parseInt(transaction.getAmount()) >= monthlyLimit * MONTHLY_LIMIT_FACTOR;
    //     }

    //     return false;
    // }
    
}
