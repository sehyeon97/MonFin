package com.sehyeon.monfin.auth.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sehyeon.monfin.bank.dto.requests.LoginRequest;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.security.LoginStatus;

import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class LoginAuthTest {

    private static final String username = "unique-username";
    private static final String password = "secure-password";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objMapper; // serialize object into json

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void createBankAccount() {
        BankAccount bankAccount = new BankAccount(
            username,
            passwordEncoder.encode(password),
            username + password,
            "8001230000"
        );
        bankRepository.save(bankAccount);
    }

    @Test
    public void shouldHaveJWTCookiesForSuccessfulLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String req = objMapper.writeValueAsString(loginRequest);

        ResultActions result = mvc.perform(
            post("/api/bank/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        );

        result.andExpect(status().isOk());
        result.andExpect(cookie().exists("access_token"));
        result.andExpect(cookie().exists("refresh_token"));
        result.andExpect(content().string(LoginStatus.SUCCESS.getLoginStatusMessage()));
    }

    @Test
    public void shouldFailToLoginWithIncorrectUsernameOrPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, passwordEncoder.encode(password));
        String req = objMapper.writeValueAsString(loginRequest);

        ResultActions result = mvc.perform(
            post("/api/bank/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        );

        result.andExpect(status().isBadRequest());
        result.andExpect(content().string(LoginStatus.FAIL.getLoginStatusMessage()));
        result.andExpect(cookie().doesNotExist("access_token"));
        result.andExpect(cookie().doesNotExist("refresh_token"));
    }
    
}
