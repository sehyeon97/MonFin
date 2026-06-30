package com.sehyeon.monfin.transaction.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sehyeon.monfin.bank.domainobjs.FraudDetectionResult;
import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.responses.CardAuthorizationResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;
import com.sehyeon.monfin.bank.repos.TransactionRepository;
import com.sehyeon.monfin.bank.services.bank.BankInboxService;
import com.sehyeon.monfin.bank.services.bank.CardService;
import com.sehyeon.monfin.bank.services.transactions.FraudDetectionService;
import com.sehyeon.monfin.bank.services.transactions.OTPValidatorService;
import com.sehyeon.monfin.bank.services.transactions.TransactionService;

/**
 * Every succeeding @Test means the preceeding test passed.
 * For example, when testing shouldDeclineInvalidCryptogram,
 * it means that a valid card token was passed.
 * All the tests that came before a test, passed its happy case.
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private CardTokenRepository cardTokenRepository;

    @Mock
    private CardService cardService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private OTPValidatorService otpService;

    @Mock
    private BankInboxService bankInboxService;

    @InjectMocks
    private TransactionService transactionService;

    private Card card;
    private CardToken cardToken;
    private CardAuthorizationRequest req;

    @BeforeEach
    public void setup() {
        BasicCardInfo info = new BasicCardInfo("null", "12", "2026", "null", "123");
        this.card = new Card(0, 0, info, null, null, null, null, 1000, 10000);
        this.card.setCardStatus(CardStatus.ACTIVE);
        this.cardToken = new CardToken("card-token", card);
        this.req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), UUID.randomUUID(), "Amazon", Instant.now(),
            10000, "cryptogram", "url");
    }

    @Test
    public void shouldDeclineInvalidCardToken() {
        // Arrange
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.empty());

        // Act
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Invalid card token.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void shouldDeclineInvalidCryptogram() {
        // Arrange
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));

        // Act
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Invalid request.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void shouldDeclineFrozenCards() {
        // Arrange
        card.setCardStatus(CardStatus.FROZEN);
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 10000;
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));

        // Act
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Card is not active.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void shouldDeclineTransactionDatePastCardExpiry() {
        BasicCardInfo info = new BasicCardInfo("1234123412341234", "5", "2026", "dough", "123");
        Card otherCard = new Card(100, 0, info, null, null, null, Instant.now(), 1000, 10000);
        otherCard.setCardStatus(CardStatus.ACTIVE);
        CardToken otherCardToken = new CardToken("token", otherCard);

        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 10000;
        String cryptogram = generateCryptogram(otherCardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), otherCardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(otherCard));

        // Act
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Invalid transaction date.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void amountShouldBeWithinDailyAndMonthlyLimit() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int highAmount = 10000;
        int lowAmount = 1000;
        BasicCardInfo info = new BasicCardInfo(
            "1234123412341234", "6", "2026", "doe", "123");

        // higher than daily limit request
        Card otherCard = new Card(
            highAmount, 0, info, null, null, null,
            timestamp, lowAmount - 100, highAmount - 100);
        CardToken otherCardToken = new CardToken("low-card-token", otherCard);
        otherCard.setCardStatus(CardStatus.ACTIVE);
        otherCard.setBankAccount(new BankAccount("Dough", "Ordoe", "Dough Doe", "Though-dough-toe"));
        String lowCryptogram = generateCryptogram(otherCardToken.getCardToken(), merchantID.toString(), timestamp, lowAmount);
        CardAuthorizationRequest reqLow = new CardAuthorizationRequest(
            UUID.randomUUID(), otherCardToken.getCardToken(), merchantID, "Amazon",
            timestamp, lowAmount, lowCryptogram, "url");

        // higher than monthly limit request (should decline)
        String highCryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, highAmount);
        CardAuthorizationRequest reqHigh = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, highAmount, highCryptogram, "url");

        when(cardTokenRepository.findByCardToken(reqLow.cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardTokenRepository.findByCardToken(reqHigh.cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardService.getCardByID(otherCard.getCardID()))
            .thenReturn(Optional.of(otherCard));

        // Act
        CardAuthorizationResponse resLow = transactionService.createCardAuthorizationResponse(reqLow);
        CardAuthorizationResponse resHigh = transactionService.createCardAuthorizationResponse(reqHigh);

        // Assert
        assertFalse(resLow.authorized());
        assertSame("", resLow.authorizationCode());
        assertEquals("Daily or monthly limit reached.", resLow.declineReason());
        assertSame("", resLow.bankCallbackUrl());

        assertFalse(resHigh.authorized());
        assertSame("", resHigh.authorizationCode());
        assertEquals("Daily or monthly limit reached.", resHigh.declineReason());
        assertSame("", resHigh.bankCallbackUrl());
    }

    @Test
    public void shouldDeclineWhenNotEnoughFunds() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 50;
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        // Act
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Not enough funds.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void shouldApproveWhenRiskIsLow() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 50;
        card.setBalance(amount * 100);
        card.setBankAccount(new BankAccount("Though", "Dough", "Toedoe todo", ""));
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        // Act
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(25, new ArrayList<>(), true));
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertTrue(res.authorized());
        assertEquals("authorized", res.authorizationCode());
        assertSame("", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    @Test
    public void shouldSendOTPOnMediumRiskScore() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 50;
        card.setBalance(amount * 100);
        card.setBankAccount(new BankAccount("Though", "Dough", "Toedoe todo", ""));
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        // Act
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(50, new ArrayList<>(), true));
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("OTP Required.", res.declineReason());
        assertEquals("http://localhost:8080/verify/otp", res.bankCallbackUrl());
    }

    @Test
    public void shouldDeclineWhenRiskHigh() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 50;
        card.setBalance(amount * 100);
        card.setBankAccount(new BankAccount("Though", "Dough", "Toedoe todo", ""));
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        CardAuthorizationRequest req = new CardAuthorizationRequest(
            UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            timestamp, amount, cryptogram, "url");

        // Act
        when(cardTokenRepository.findByCardToken(req.cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(75, new ArrayList<>(), false));
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);

        // Assert
        assertFalse(res.authorized());
        assertSame("", res.authorizationCode());
        assertEquals("Fraud detected.", res.declineReason());
        assertSame("", res.bankCallbackUrl());
    }

    private String generateCryptogram(String cardToken, String merchantID, Instant timestamp, int amount) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec spec = new SecretKeySpec(cardToken.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(spec);

            String params = 
                merchantID + "|" + timestamp.toString() + "|" + Integer.toString(amount);
            byte[] hmac = mac.doFinal(params.getBytes(StandardCharsets.UTF_8));

            return bytestoHexString(hmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String bytestoHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
    
}
