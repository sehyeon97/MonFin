package com.sehyeon.monfin.transaction.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import com.sehyeon.monfin.bank.controllers.TransactionController;
import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.requests.VerifyOTPRequest;
import com.sehyeon.monfin.bank.dto.responses.CardAuthorizationResponse;
import com.sehyeon.monfin.bank.dto.responses.VerifyOTPResponse;
import com.sehyeon.monfin.bank.services.transactions.OTPValidatorService;
import com.sehyeon.monfin.bank.services.transactions.TransactionService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    private static final String REQUEST_MAPPING_ENDPOINT = "/api/bank/transactions";
    private static final String AUTHORIZE_TRANSACTION_ENDPOINT = "/authorize";
    private static final String VERIFY_OTP_ENDPOINT = "/verify-otp";

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private OTPValidatorService otpValidatorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    private CardAuthorizationRequest req;
    private VerifyOTPRequest otpReq;
    private UUID transactionID;

    @BeforeEach
    public void setup() {
        req = new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID(), "Amazon",
            Instant.now(), 100, "cryptogram", "link");
        
        transactionID = UUID.randomUUID();
        otpReq = new VerifyOTPRequest(transactionID, "123456");
    }

    @Test
    public void shouldAuthorizeTransaction() throws Exception {
        // Arrange
        CardAuthorizationResponse resApproved = new CardAuthorizationResponse(
            true, "authorized", "", "");

        // Act
        when(transactionService.createCardAuthorizationResponse(req))
            .thenReturn(resApproved);

        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + AUTHORIZE_TRANSACTION_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(req)));
        
        // Assert
        verify(transactionService).createCardAuthorizationResponse(req);
        result.andExpect(status().isOk());
        result.andExpect(content().string("authorized"));
    }

    @Test
    public void shouldDeclineTransaction() throws Exception {
        // Arrange
        CardAuthorizationResponse resDeclined = new CardAuthorizationResponse(
            false, "", "OTP Required.", "bank_callback_url");

        // Act
        when(transactionService.createCardAuthorizationResponse(req))
            .thenReturn(resDeclined);

        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + AUTHORIZE_TRANSACTION_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(req)));

        // Assert
        verify(transactionService).createCardAuthorizationResponse(req);
        result.andExpect(status().isBadRequest());
        result.andExpect(content().string("OTP Required."));
    }

    @Test
    public void shouldAcceptOTP() throws Exception {
        // Arrange
        VerifyOTPResponse resAccepted = new VerifyOTPResponse(true, "authorized");

        // Act
        when(otpValidatorService.validateOTP(any(), any()))
            .thenReturn(resAccepted);
        
        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + VERIFY_OTP_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(otpReq)));

        // Assert
        verify(otpValidatorService).validateOTP(transactionID, "123456");
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.verified").value(true));
        result.andExpect(jsonPath("$.authorizationCode").value("authorized"));
    }

    @Test
    public void shouldDeclineOTP() throws Exception {
        // Arrange
        VerifyOTPResponse resDeclined = new VerifyOTPResponse(false, "");

        // Act
        when(otpValidatorService.validateOTP(any(), any()))
            .thenReturn(resDeclined);

        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + VERIFY_OTP_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(otpReq)));

        // Assert
        verify(otpValidatorService).validateOTP(transactionID, "123456");
        result.andExpect(status().isUnauthorized());
        result.andExpect(jsonPath("$.verified").value(false));
        result.andExpect(jsonPath("$.authorizationCode").value(""));
    }
    
}
