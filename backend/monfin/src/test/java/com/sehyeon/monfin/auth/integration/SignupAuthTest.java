package com.sehyeon.monfin.auth.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.security.SignupStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class SignupAuthTest {

    @Autowired
    private MockMvc mvc;

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

    @Test
    public void shouldSetJWTCookiesForSuccessfulSignup() throws Exception {
        String req = """
            {
                "username": "username",
                "password": "password",
                "fullName": "Test User",
                "phoneNumber": "1234567890"
            }
        """;
        
        ResultActions result = mvc.perform(
            post("/api/bank/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        );
        
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(cookie().exists("access_token"));
        result.andExpect(cookie().exists("refresh_token"));
        result.andExpect(content().string(SignupStatus.SUCCESS.getSignupStatusMessage()));

        Optional<BankAccount> bankAccount = bankRepository.findByUsername("username");
        assertFalse(bankAccount.isEmpty());

        // stored password should be encrypted
        assertNotEquals("password", bankAccount.get().getPassword());
    }

    @Test
    public void shouldFailSignupWhenPhoneNumberIsAlreadyInUse() throws Exception {
        String username = "username";
        String password = "password";
        String fullname = "name";
        String phoneNumber = "8001230000";
        BankAccount existingAccount = new BankAccount(username, password, fullname, phoneNumber);
        bankRepository.save(existingAccount);

        String req = """
            {
                "username": "username",
                "password": "password",
                "fullName": "name",
                "phoneNumber": "8001230000"
            }
        """;

        ResultActions result = mvc.perform(
            post("/api/bank/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        );

        result.andExpect(status().isNotAcceptable());
        result.andExpect(cookie().doesNotExist("access_token"));
        result.andExpect(cookie().doesNotExist("refresh_token"));
        result.andExpect(content().string(SignupStatus.FAIL.getSignupStatusMessage()));
    }
    
}
