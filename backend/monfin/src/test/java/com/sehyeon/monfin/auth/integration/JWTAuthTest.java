package com.sehyeon.monfin.auth.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sehyeon.monfin.bank.dto.requests.NewCardRequest;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.services.auth.JwtService;

import jakarta.servlet.http.Cookie;
import tools.jackson.databind.ObjectMapper;

/** The process:
 * 1. Frontend makes request such as:
 *      POST /api/bank/something/something
 *      Cookie: access_token=<JWT1>, refresh_token=<JWT2>
 * 2. Spring Security Filter Chain
 * 3. JwtAuthenticationFilter
 * 4. Extract JWT from cookie
 * 5. JwtService.parseJwt() and get bank account id from claims.get("id")
 * 6. Create Authentication object with loaded Bank Account
 * 7. SecurityContextHolder.setAuthentication(authentication)
 * 8. Respective controller called
 * 9. Return correct ResponseEntity
 * This Test Class actually tests BankController creating card feature
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class JWTAuthTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private JwtService jwtService;

    private BankAccount bankAccount;
    @Autowired
    private BankRepository bankRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void setup() {
        createBankAccount();
    }

    private void createBankAccount() {
        bankAccount = new BankAccount(
            "username",
            "password",
            "name",
            "9091230000"
        );
        bankRepository.save(bankAccount);
    }

    @Test
    public void shouldAddNewCardToBankAccount() throws Exception {
        // bank account id is deprecated with the use of JWT. DTO has not been refactored yet
        NewCardRequest newCardRequest = new NewCardRequest(
            bankAccount.getBankAccountID(),
            "DEBIT",
            "VISA",
            "GOLD"
        );
        String req = objMapper.writeValueAsString(newCardRequest);
        String accessToken =
            jwtService.createJwtAccessRefreshFor(bankAccount.getBankAccountID()).split("\\|")[0];

        ResultActions result = mvc.perform(
            post("/api/bank/accounts/cards/create")
                .with(csrf())
                .cookie(new Cookie("access_token", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.message").value("Successfully added card to account."));
    }
    
}
