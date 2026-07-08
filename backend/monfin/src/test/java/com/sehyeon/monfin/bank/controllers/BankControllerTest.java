package com.sehyeon.monfin.bank.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import com.sehyeon.monfin.bank.dto.requests.NewCardRequest;
import com.sehyeon.monfin.bank.dto.responses.NewCardResponse;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.UserCredentials;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.repos.CardRepository;

import tools.jackson.databind.ObjectMapper;

// @WebMvcTest(BankController.class) THIS WOULD ONLY USE THE CONTROLLER CLASS WHICH WOULD FAIL IN COMPILER
//                                  BECAUSE BANKCONTROLLER NEEDS OTHER SERVICES TO RUN PROPERLY
// Therefore, use SpringBootTest to get the full Spring context
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
// For integration test with real sql container
// everything related to this annotation will have "*DB" marked next to it for learning purposes
@Testcontainers
public class BankControllerTest {

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

    // Modern fintech use this instead of RestTestTemplate
    private WebTestClient webTestClient;

    // Because we used @DynamicPropertySource to use dockerized postgresql,
    // any database interactions will use this test database instead of my real postgresql
    // when done running the test, it will destroy this containerized postgresql
    @Autowired
    private BankRepository bankRepository;

    // To test that the card was successfully added to the bank account
    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // provided by spring boot test library

    @Autowired
    Environment env;

    // THIS IS FOR TESTING PURPOSES. SPECIFICALLY FOR getBankAccountPass() TEST
    private BankAccount savedJohnDoe;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
 
        // need to delete cards table first because the foreign key must be deleted first
        cardRepository.deleteAll();
        bankRepository.deleteAll();

        this.savedJohnDoe = bankRepository.save(
            new BankAccount("John", "Doe", "John Doe", "9096773328"));
    }

    @Test
    public void getBankAccountPass() throws Exception {
        UserCredentials userCredentials = new UserCredentials("John", "Doe");

        // Act & Assert
        webTestClient.post()
            .uri("/api/bank/accounts/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userCredentials)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UUID.class)
            .value(body -> {
                assertThat(body).isEqualTo(savedJohnDoe.getBankAccountID());
            });
    }

    @Test
    public void getBankAccountFail() throws Exception {
        UserCredentials userCredentials = new UserCredentials("Bob", "Builder");

        String requestBody = objectMapper.writeValueAsString(userCredentials);

        mockMvc.perform(post("/api/bank/accounts/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void checkProperty() {
        System.out.println(env.getProperty("crypto.fpe.key"));
    }

    @Test
    public void resourceExists() {
        assertNotNull(
            getClass().getClassLoader().getResource("application.yaml")
        );
    }

    @Test // happy path (adding card to account)
    public void shouldAddCardToBankAccount() throws Exception {
        // Arrange
        Optional<BankAccount> account = bankRepository.findByUsernameAndPassword("John", "Doe");
        BankAccount bankAccount = account.get();
        NewCardRequest req = new NewCardRequest(
            bankAccount.getBankAccountID(), "credit", "Visa", "SILVER");
        
        // Act
        ResultActions result = mockMvc.perform(
            post("/api/bank/accounts/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        );

        // Assert
        result.andExpect(status().isOk());
        NewCardResponse response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), NewCardResponse.class);
        assertThat(response.message().equalsIgnoreCase("Successfully added card to account"));
    }

    @Test // sad path (adding card to account)
    public void shouldFailToAddCardToBankAccount() throws Exception {
        // Arrange
        NewCardRequest req = new NewCardRequest(
            UUID.randomUUID(), "credit", "VISA", "Silver");

        // Act
        ResultActions result = mockMvc.perform(
            post("/api/bank/accounts/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        );

        // Assert
        result.andExpect(status().isNotFound());
        NewCardResponse response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), NewCardResponse.class);
        assertThat(response.message().equalsIgnoreCase("Invalid bank account"));
    }

    @Test
    public void addedCardShouldExistInCardRepository() throws Exception {
        // Arrange
        NewCardRequest req = new NewCardRequest(
            savedJohnDoe.getBankAccountID(),"credit", "Visa", "SILVER");

        // Act
        mockMvc.perform(
            post("/api/bank/accounts/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        );

        // Assert
        Optional<Card> card = cardRepository.findCardByBankAccount_BankAccountID(savedJohnDoe.getBankAccountID());
        assertTrue(card.isPresent());

        Card cardData = card.get();
        assertThat(cardData.getCardType() == CardType.CREDIT);
        assertThat(cardData.getCardNetwork() == CardNetwork.VISA);
        assertThat(cardData.getCardTier() == CardTier.SILVER);
        assertThat(cardData.getCardNetwork() != CardNetwork.DISCOVER);
    }
    
}
