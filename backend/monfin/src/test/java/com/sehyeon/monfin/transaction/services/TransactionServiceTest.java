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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;
import com.sehyeon.monfin.bank.repos.transactions.TransactionRepository;
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
    private List<CardAuthorizationRequest> req = new ArrayList<>();

    @BeforeEach
    public void setup() {
        BasicCardInfo info = new BasicCardInfo("null", "12", "2026", "null", "123");
        this.card = new Card(0, 0, info, null, null, null, null, 1000, 10000);
        this.card.setCardStatus(CardStatus.ACTIVE);
        this.cardToken = new CardToken("card-token", card);
        this.req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), UUID.randomUUID(), "Amazon",
            "Brand", "ProductName", Instant.now(), 10000, "cryptogram", "", ""));
    }

    @Test
    public void shouldDeclineInvalidCardToken() {
        // Arrange
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.empty());

        // Act
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);

        // Assert
        CardAuthorizationResponse element = res.get(0).resData();
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Invalid card token.", element.declineReason());
        assertSame("", element.url());
    }

    @Test
    public void shouldDeclineInvalidCryptogram() {
        // Arrange
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), UUID.randomUUID(), "Amazon",
            "Brand", "product name",
            Instant.now().minus(Duration.ofDays(1)), // diff timestamp yields diff cryptogram
            10000, "cryptogram", "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(any()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(any()))
            .thenReturn(Optional.of(card));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Invalid request.", element.declineReason());
        assertSame("", element.url());
    }

    @Test
    public void shouldDeclineFrozenCards() {
        // Arrange
        card.setCardStatus(CardStatus.FROZEN);
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 10000;
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            "brand", "name", timestamp, amount, cryptogram, "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Card is not active.", element.declineReason());
        assertSame("", element.url());
    }

    @Test
    public void shouldDeclineTransactionDatePastCardExpiry() {
        BasicCardInfo info = new BasicCardInfo(
            "1234123412341234", "5", "2026", "dough", "123");
        Card otherCard = new Card(
            100, 0, info, null, null, null, Instant.now(), 1000, 10000);
        otherCard.setCardStatus(CardStatus.ACTIVE);
        CardToken otherCardToken = new CardToken("token", otherCard);

        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 10000;
        String cryptogram = generateCryptogram(otherCardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), otherCardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", timestamp, amount, cryptogram, "", ""));

        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(otherCard));

        // Act
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Invalid transaction date.", element.declineReason());
        assertSame("", element.url());
    }

    @Test
    public void amountShouldBeWithinDailyAndMonthlyLimit() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        Instant lastMonthTimestamp = timestamp.minus(Duration.ofDays(32));
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
        String lowCryptogram = generateCryptogram(otherCardToken.getCardToken(), merchantID.toString(), lastMonthTimestamp, lowAmount);
        List<CardAuthorizationRequest> reqLow = new ArrayList<>();
        reqLow.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), otherCardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", lastMonthTimestamp, lowAmount, lowCryptogram, "", ""));

        // higher than monthly limit request (should decline)
        String highCryptogram = generateCryptogram(otherCardToken.getCardToken(), merchantID.toString(), lastMonthTimestamp, highAmount);
        List<CardAuthorizationRequest> reqHigh = new ArrayList<>();
        reqHigh.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), otherCardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", lastMonthTimestamp, highAmount, highCryptogram, "", ""));

        when(cardTokenRepository.findByCardToken(reqLow.get(0).cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardTokenRepository.findByCardToken(reqHigh.get(0).cardToken()))
            .thenReturn(Optional.of(otherCardToken));
        when(cardService.getCardByID(otherCard.getCardID()))
            .thenReturn(Optional.of(otherCard));

        // Act
        List<TransactionResponse> resLow = transactionService.createCardAuthorizationResponses(reqLow);
        CardAuthorizationResponse resLowElement = resLow.get(0).resData();
        List<TransactionResponse> resHigh = transactionService.createCardAuthorizationResponses(reqHigh);
        CardAuthorizationResponse resHighElement = resHigh.get(0).resData();

        // Assert
        assertFalse(resLowElement.authorized());
        assertSame("", resLowElement.authorizationCode());
        assertEquals("Daily or monthly limit reached.", resLowElement.declineReason());
        assertSame("", resLowElement.url());

        assertFalse(resHighElement.authorized());
        assertSame("", resHighElement.authorizationCode());
        assertEquals("Daily or monthly limit reached.", resHighElement.declineReason());
        assertSame("", resHighElement.url());
    }

    @Test
    public void shouldDeclineWhenNotEnoughFunds() {
        // Arrange
        UUID merchantID = UUID.randomUUID();
        Instant timestamp = Instant.now();
        int amount = 50;
        String cryptogram = generateCryptogram(cardToken.getCardToken(), merchantID.toString(), timestamp, amount);
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", timestamp, amount, cryptogram, "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Not enough funds.", element.declineReason());
        assertSame("", element.url());
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
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", timestamp, amount, cryptogram, "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(25, new ArrayList<>(), true));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertTrue(element.authorized());
        assertEquals("authorized", element.authorizationCode());
        assertSame("", element.declineReason());
        assertSame("", element.url());
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
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", timestamp, amount, cryptogram, "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(50, new ArrayList<>(), true));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("OTP Required.", element.declineReason());
        // assertEquals("http://localhost:8080/verify/otp" + "", element.bankCallbackUrl());
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
        List<CardAuthorizationRequest> req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), cardToken.getCardToken(), merchantID, "Amazon",
            "brand", "product name", timestamp, amount, cryptogram, "", ""));

        // Act
        when(cardTokenRepository.findByCardToken(req.get(0).cardToken()))
            .thenReturn(Optional.of(cardToken));
        when(cardService.getCardByID(card.getCardID()))
            .thenReturn(Optional.of(card));
        when(fraudDetectionService.evaluate(any(), any(), any()))
            .thenReturn(new FraudDetectionResult(75, new ArrayList<>(), false));
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        CardAuthorizationResponse element = res.get(0).resData();

        // Assert
        assertFalse(element.authorized());
        assertSame("", element.authorizationCode());
        assertEquals("Fraud detected.", element.declineReason());
        assertSame("", element.url());
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
