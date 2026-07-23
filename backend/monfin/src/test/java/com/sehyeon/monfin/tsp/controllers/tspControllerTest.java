package com.sehyeon.monfin.tsp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sehyeon.monfin.bank.controllers.TokenServiceProviderController;
import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.repos.CardRepository;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;
import com.sehyeon.monfin.bank.services.card.CardIssuanceService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class tspControllerTest {

    // setup docker
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    // to create a bank account
    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardTokenRepository tokenRepository;

    // to add card to the bank account
    @Autowired
    private CardIssuanceService cardIssuer;

    @InjectMocks
    private TokenServiceProviderController tspController;

    private BasicCardInfo basicCardInfo;

    // send request to controller without starting server
    @Autowired
    private MockMvc mockMvc;

    // to translate the dto into a json
    @Autowired
    private ObjectMapper objMapper;

    private static final String URI_ENDPOINT = "/api/bank/payment/processor/credentials";

    // Create a testable account
    @BeforeEach
    public void init() {
        // reset
        bankRepository.deleteAll();
        tokenRepository.deleteAll(); // tokens have to be deleted before the card gets deleted
        cardRepository.deleteAll();
    }

    @Test
    @Rollback(true)
    public void shouldTokenizeRequest() throws Exception {
        // arrange
        // populate the database with a bank account
        BankAccount bankAccount = new BankAccount("test", "containers", "poo ding", "9096773328");
        bankRepository.saveAndFlush(bankAccount);

        // add a card to this bank account
        Card card = cardIssuer.issueCard(bankAccount, "poo ding", CardType.CREDIT, CardNetwork.VISA, CardTier.GOLD);
        System.out.println("PAN ON ISSUE CARD: " + card.getBasicCardInfo().getPAN());
        cardRepository.saveAndFlush(card);
        System.out.println(cardRepository.count()); // should be 1
        this.basicCardInfo = card.getBasicCardInfo();

        CardTokenizationRequest request = new CardTokenizationRequest(
            basicCardInfo.getPAN(), basicCardInfo.getSecurityCode(), basicCardInfo.getFullName(),
            basicCardInfo.getExpMonth(), basicCardInfo.getExpYear()
        );

        // act
        ResultActions resultActions = mockMvc.perform(
            post(URI_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(request))
        );

        // checks
        // make sure bank account does exist (passed)
        Optional<BankAccount> acc = bankRepository.findByUsernameAndPassword("test", "containers");
        assertThat(acc.isPresent());
        // make sure card exists (passed)
        Optional<Card> savedCard = cardRepository.findCardByBasicCardInfo(basicCardInfo);
        assertThat(savedCard).isPresent();

        // assert
        resultActions.andExpect(status().isOk());
    }

    @Test
    @Rollback(true)
    public void shouldFailToTokenize() throws Exception {
        // Arrange
        CardTokenizationRequest request = new CardTokenizationRequest(
            URI_ENDPOINT, URI_ENDPOINT, "J W", "6", "2031");

        // Act
        ResultActions resultActions = mockMvc.perform(
            post(URI_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objMapper.writeValueAsString(request))   
        );

        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    
}
