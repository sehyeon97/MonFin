package com.sehyeon.monfin.bank.services.transactions;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.domainobjs.FraudDetectionResult;
import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.responses.CardAuthorizationResponse;
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.transactions.Transaction;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.model.limits.SpendingLimits;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;
import com.sehyeon.monfin.bank.repos.transactions.TransactionRepository;
import com.sehyeon.monfin.bank.services.bank.BankInboxService;
import com.sehyeon.monfin.bank.services.bank.CardService;

/**
 * When Payment Processors send over card token with transaction details,
 * generate a response when card token is valid whether the transaction
 * can be approved or declined.
 * When approved, the money sits with the bank,
 * until the Payment Processor requests for payment
 * via the authorization code within a certain number of days.
 * 
 * Things to check:
 * 1. Does the card exist? (verify with card token)
 * 2. Is the cryptogram valid?
 * (verify that HMAC on cardToken, merchantID, timestamp, and amount is the same cryptogram received)
 * 3. Is the card (Status) ACTIVE?
 * 4. Is today's date before the expire date?
 * 5. Is the amount within the daily and monthly limits?
 * 6. Are there enough funds to charge the card? (Debit | Credit separation)?
 * 7. Is this a suspicious transaction? (Run fraud algorithms)
 * 
 * FraudDetectionService.java will handle the below
 * FRAUD RULES (passing them will increase the riskScore which may freeze a card)
 * A. Too many transactions (2+) within the last minute (utilize timestamp and db records)
 * B. Amount is higher than user's average expense
 * C. Same card used in 3+ different merchants
 * D. (Not in the scope of MVP): Geographically impossible to make purchases in two distant places
 * E. 3+ repeated declines
 * F. Spending spike for the day compared to average daily spending
 * G. New merchant, but amount is over $1,000
 */
@Service
public class TransactionService {

    // retrieve cardID by cardtoken
    @Autowired
    private CardTokenRepository tokenRepository;

    // retrieve card by card ID
    @Autowired
    private CardService cardService;

    // retrieve all transactions made by card to get the current total monthly spending limit
    @Autowired
    private TransactionRepository transactionRepository;

    // Make sure transaction is fraud-proof
    @Autowired
    private FraudDetectionService fraudDetectionService;

    // If needed, store OTP
    @Autowired
    private OTPValidatorService otpService;

    // If needed, after storing OTP, send the OTP to customer's phone number
    @Autowired
    private BankInboxService bankInboxService;

    // constants
    private static final ZoneId zone = ZoneId.of("America/Los_Angeles"); // PST
    private static final int RISK_SCORE_LOWER_LIMIT = 30;
    private static final int RISK_SCORE_UPPER_LIMIT = 70;
    private static final int DECREASE_RISK_SCORE_ON_APPROVE = 5;
    private static final int INCREASE_RISK_SCORE_ON_DECLINE = 20;
    private static final int HIGH_RISK_SCORE_FREEZE_CARD = 85;
    private static final String BANK_CALLBACK_URL = "http://localhost:8080/verify/otp";

    public TransactionService() {}

    // public List<CardAuthorizationResponse> createCardAuthorizationResponses(List<CardAuthorizationRequest> requests) {
    //     List<CardAuthorizationResponse> responses = new ArrayList<>();
    //     for (CardAuthorizationRequest req : requests) {
    //         responses.add(createCardAuthorizationResponse(req));
    //     }
    //     return responses;
    // }

    public List<TransactionResponse> createCardAuthorizationResponses(List<CardAuthorizationRequest> requests) {
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        UUID otpID = null;
        Card card = null;
        for (CardAuthorizationRequest req :  requests) {
            UUID transactionID = req.transactionID();
            // validate card token by checking that a card is mapped to this token
            Optional<CardToken> cardTokenData = tokenRepository.findByCardToken(req.cardToken());
            if (cardTokenData.isEmpty()) { // invalid card token
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Invalid card token.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            Optional<Card> cardData = cardService.getCardByID(cardTokenData.get().getCardID());
            card = cardData.get();
            CardToken cardToken = cardTokenData.get();

            // validate cryptogram using HMAC (assume the cryptogram in request also used HMAC)
            String expectedCryptogram = generateCryptogram(req.cardToken(), req.merchantID().toString(), req.timestamp(), req.amount());
            if (!expectedCryptogram.equalsIgnoreCase(req.cryptogram())) {
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Invalid request.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            // validate card status is ACTIVE
            if (card.getCardStatus() != CardStatus.ACTIVE) {
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Card is not active.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            // confirm transaction date is before the card's expire month & year
            BasicCardInfo cardInfo = card.getBasicCardInfo();
            int comparedValue = compare(
                req.timestamp(), Integer.parseInt(cardInfo.getExpMonth()), Integer.parseInt(cardInfo.getExpYear()));
            
            if (comparedValue > 0) {
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Invalid transaction date.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            // check that amount is within the daily and monthly spending limits
            int dailyLimit = card.getDailyLimit();
            int monthlyLimit = card.getMonthlyLimit();
            List<Transaction> thisMonthTransactions = getThisMonthTransactions(cardToken.getCardToken());
            SpendingLimits totalExpenses = calculateTotalTransactionAmount(cardToken.getCardToken(), thisMonthTransactions);

            if (hasCardReachedSpendingLimit(totalExpenses, dailyLimit, monthlyLimit, req.amount())) {
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Daily or monthly limit reached.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            // Ensure enough funds are present
            if (!hasEnoughAvailableBalance(card, req.amount())) {
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Not enough funds.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
                continue;
            }

            // Run Transaction against Fraud Prevention Rules
            ZonedDateTime timestampZDT = convertInstantToZonedDateTime(req.timestamp());
            Transaction transaction = new Transaction(
                transactionID, card.getCardID(), card.getBankAccountID(), cardToken.getCardToken(),
                intToStr(timestampZDT.getDayOfMonth()), intToStr(timestampZDT.getMonthValue()), intToStr(timestampZDT.getYear()),
                req.merchantName(), req.merchantID(), req.amount(), req.timestamp(), "");
            FraudDetectionResult result = fraudDetectionService.evaluate(thisMonthTransactions, transaction, card);

            int fraudScore = result.riskScore();
            if (fraudScore <= 30) { // approved
                card.setRiskScore(card.getRiskScore() - DECREASE_RISK_SCORE_ON_APPROVE);
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    true, "authorized", "", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
            // } else if (fraudScore <= 30) { IF YOU WANT INTEGRATION TESTING TO RETURN OTP REQUIRED & set above to <= 0
            } else if (fraudScore > RISK_SCORE_LOWER_LIMIT && fraudScore <= RISK_SCORE_UPPER_LIMIT) {
                if (otpID == null) {
                    otpID = otpService.storeOTP(transaction.getID(), req.callbackUrl());
                } else {
                    otpService.addTransactionToOTP(otpID, transactionID);
                }
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "OTP Required.", BANK_CALLBACK_URL + "/" + otpID);
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
            } else { // declined
                int cardRiskScore = card.getRiskScore();
                card.setRiskScore(cardRiskScore + INCREASE_RISK_SCORE_ON_DECLINE);
                if (cardRiskScore >= HIGH_RISK_SCORE_FREEZE_CARD) {
                    card.setCardStatus(CardStatus.FROZEN);
                    bankInboxService.sendCardStatus(card.getBankAccountID(), card.getCardStatus().toString(), transaction);
                }
                CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                    false, "", "Fraud detected.", "");
                TransactionResponse res = new TransactionResponse(transactionID, caRes);
                transactionResponses.add(res);
            }
        }
        
        if (otpID != null) {
            bankInboxService.sendOTP(card.getBankAccountID(), otpService.getOTPStringByID(otpID));
        }
        return transactionResponses;
    }

    private String generateCryptogram(String cardToken, String merchantID, Instant timestamp, int amount) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec spec = new SecretKeySpec(cardToken.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(spec);

            String params = 
                merchantID + "|" + timestamp.toString() + "|" + Integer.toString(amount);
            byte[] hmac = mac.doFinal(params.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    // Returns a negative number if transaction timestamp is BEFORE the card expire date
    // Returns a positive number if transaction timestamp is AFTER the card expire date
    // Returns 0 if transaction timestamp's month/year is equal to card expire month/year
    private int compare(Instant transactionTimestamp, int cardExpMonth, int cardExpYear) {
        ZonedDateTime transactionDate = transactionTimestamp.atZone(zone);
        int transactionMonth = transactionDate.getMonthValue();
        int transactionYear = transactionDate.getYear();

        // happy path. transaction date is before card expire date
        if (transactionYear < cardExpYear || (transactionYear == cardExpYear && transactionMonth <= cardExpMonth)) {
            return -1;
        } else if (transactionYear > cardExpYear || (transactionYear == cardExpYear && transactionMonth > cardExpMonth)) {
            // sad path. transaction date is after card expire date
            return 1;
        } else {
            return 0;
        }
    }

    // utilizing spending limits here to avoid recreating an object that holds a daily and a monthly amount
    private SpendingLimits calculateTotalTransactionAmount(String cardToken, List<Transaction> transactions) {
        int dailyTotal = 0;
        int monthlyTotal = 0;
        for (Transaction transaction : transactions) {
            int amount = Integer.parseInt(transaction.getAmount());
            ZonedDateTime transactionDay = ZonedDateTime.ofInstant(transaction.getTimestamp(), zone);

            if (transactionDay.getDayOfMonth() == ZonedDateTime.now(zone).getDayOfMonth()) {
                dailyTotal += amount;
            }
            monthlyTotal += amount;
        }
        return new SpendingLimits(dailyTotal, monthlyTotal);
    }

    private List<Transaction> getThisMonthTransactions(String cardToken) {
        ZonedDateTime today = ZonedDateTime.now(zone);
        ZonedDateTime startOfMonth = 
            today.withDayOfMonth(1) // start day is always 1st of the month
                .toLocalDate() // removes the time and leaves only the month, day, year in MM-DD-YYYY format
                .atStartOfDay(zone); // gets today's month
        ZonedDateTime startOfNextMonth = startOfMonth.plusMonths(1); // exclusive

        return transactionRepository
            .findByCardTokenAndTimestampBetweenOrderByTimestampDesc(cardToken, startOfMonth.toInstant(), startOfNextMonth.toInstant());
    }

    // returns true if card has either reached daily or monthly limit, otherwise returns false
    private boolean hasCardReachedSpendingLimit(SpendingLimits currentExpenses, int cardDailyLimit, int cardMonthlyLimit, int amount) {
        int currentDailyExpenses = currentExpenses.getDailyAmountLimit();
        int currentMonthlyExpenses = currentExpenses.getMonthlyAmountLimit();

        return amount > cardDailyLimit || amount > cardMonthlyLimit || 
            currentDailyExpenses >= cardDailyLimit || currentMonthlyExpenses >= cardMonthlyLimit;
    }

    private ZonedDateTime convertInstantToZonedDateTime(Instant timestamp) {
        return ZonedDateTime.ofInstant(timestamp, zone);
    }

    private String intToStr(int number) {
        return Integer.toString(number);
    }

    // TODO: RIGHT NOW, THERE ARE ONLY DEBIT FUNCTIONALITIES AND NO CREDIT
    // checks if a credit card has enough credit to not reach or go over credit limit
    // and if a debit has enough balance without going negative
    private boolean hasEnoughAvailableBalance(Card card, int amount) {
        // if (card.getCardType() == CardType.CREDIT) {}
        return card.getBalance() - amount >= 0;
    }
    
}
