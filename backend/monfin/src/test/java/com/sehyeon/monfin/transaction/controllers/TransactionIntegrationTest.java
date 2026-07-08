package com.sehyeon.monfin.transaction.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.repos.CardRepository;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class TransactionIntegrationTest {

    /**
     * This database is shared per class
     * Without static, it would be one fresh database per Test method
     * The Container is destroyed after the Class finishes all the Test methods
     * Next Run Test on the entire class gets a new container (fresh database)
     * This means that @Rollback on methods and @Transactional on class is not needed
     * 
     * For this test to send an OTP Required response,
     * I may need to refactor my fraud prevention rule to bump down the score required to send OTP
     */
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    private WebTestClient client;
    private List<CardAuthorizationRequest> req;
    private String cardTokenFromPaymentProcessor;

    private UUID[] transactionIDs = new UUID[20];

    @Autowired
    private CardTokenRepository cardTokenRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private BankRepository bankAccountRepository;

    private static final UUID merchantID = UUID.randomUUID();
    private static final String merchantName = "Amazon";
    private static final String expMonth = "12";
    private static final String expYear = "2026";
    private static final int CARD_BALANCE = 100000; // $1,000.00
    private static final int DAILY_LIMIT = (int) (CARD_BALANCE * 0.25); // 25% of card balance
    private static final int MONTHLY_LIMIT = (int) (CARD_BALANCE * 0.75); // 75% of card balance

    private static final String REQUEST_MAPPING = "/api/bank/transactions";
    private static final String POST_MAPPING_AUTHORIZE = "/authorize";
    private static final String POST_MAPPING_VERIFY_OTP = "/verify-otp";

    @BeforeEach
    public void setup() {
        client = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        populateTablesAndFields();
        req = populateTransactionAuthorizationReq();
    }

    @Test
    public void authorizeTransactionTest() {
        ResponseSpec res = client.post()
            .uri(REQUEST_MAPPING + POST_MAPPING_AUTHORIZE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req).exchange();

        res.expectStatus().isOk();
        List<TransactionResponse> bankResponse =
            res.expectBody(new ParameterizedTypeReference<List<TransactionResponse>>() {}).returnResult().getResponseBody();
        
        assertNotNull(bankResponse);

        int count = 1;
        System.out.println("--------------------------------------------------------");
        for (TransactionResponse bankRes : bankResponse) {
            System.out.println("For Transaction " + count++ + ":");
            System.out.println("    Is Authorized?: " + bankRes.resData().authorized());
            System.out.println("    Authorization Code: " + bankRes.resData().authorizationCode());
            System.out.println("    Decline Reason: " + bankRes.resData().declineReason());
            System.out.println("    Bank callback url: " + bankRes.resData().bankCallbackUrl());
            System.out.println("--------------------------------------------------------");
        }
    }

    @Test
    public void verifyOTPTest() {}

    private void populateTablesAndFields() {
        // Customer's bank account
        BankAccount bankAccount = new BankAccount(
            "Integration", "Testing", "End ToEnd", "9096773328");

        // Create customer's card
        BasicCardInfo basicCardInfo = new BasicCardInfo("1234123412341234",
            expMonth, expYear, "Not Scammer", "123");
        Card card = new Card(CARD_BALANCE, 0, basicCardInfo,
            CardType.DEBIT, CardNetwork.MASTERCARD, CardTier.GOLD,
            Instant.now(), DAILY_LIMIT, MONTHLY_LIMIT);
        card.setCardStatus(CardStatus.ACTIVE);

        // tie customer's card with customer's bank account
        bankAccount.addCard(card);
        bankAccountRepository.save(bankAccount);
        cardRepository.save(card);

        // The card token mapped to customer's card that was given to payment processor
        CardToken cardToken = new CardToken("tok_card-token", card);
        cardTokenRepository.save(cardToken);
        cardTokenFromPaymentProcessor = cardToken.getCardToken();

        // transaction repository is not included in autowires
        // because that is an effect of calling the transaction service, not a prerequisite

        // otp repository is not included as well
        // it should be part of the response obtained from authorizeTransaction() if otp is required
        // same case for TransactionOTPRepository and BankAccountInboxRepository
    }

    private List<CardAuthorizationRequest> populateTransactionAuthorizationReq() {
        List<CardAuthorizationRequest> requests = new ArrayList<>();

        for (int i = 0; i < transactionIDs.length; i++) {
            UUID transactionID = UUID.randomUUID();
            transactionIDs[i] = transactionID;

            Instant today = Instant.now();
            Instant aPreviousDay = today.minus(Duration.ofDays(i - 1));

            int amount = new Random().nextInt(DAILY_LIMIT / 2);
            String cryptogram = generateCryptogram(cardTokenFromPaymentProcessor, merchantID.toString(), aPreviousDay, amount);
            System.out.println("--------------------------------------------------------");
            System.out.println("For Transaction " + i + 1 + ": Amount = " + amount);

            CardAuthorizationRequest request = new CardAuthorizationRequest(
                transactionID, cardTokenFromPaymentProcessor, merchantID,
                merchantName, aPreviousDay, amount, cryptogram, "payment_processor_callback/{customerID}");
            requests.add(request);
        }
        System.out.println("--------------------------------------------------------");

        return requests;
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
    
}
