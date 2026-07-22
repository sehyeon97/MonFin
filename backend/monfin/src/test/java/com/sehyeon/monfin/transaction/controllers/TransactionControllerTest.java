package com.sehyeon.monfin.transaction.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
import com.sehyeon.monfin.bank.dto.responses.TransactionData;
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
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

    private List<CardAuthorizationRequest> req;
    private VerifyOTPRequest otpReq;
    private UUID transactionID;
    private TransactionData transactionData;

    @BeforeEach
    public void setup() {
        req = new ArrayList<>();
        req.add(new CardAuthorizationRequest(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID(), "Amazon",
            "Marly", "Herod", Instant.now(), 100, "cryptogram", "", ""));
        
        transactionID = UUID.randomUUID();
        transactionData = new TransactionData(
            transactionID, UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID(), "Walmart",
            "Fresh", "Apples", Instant.now(), 100);
        otpReq = new VerifyOTPRequest(transactionID, "123456", transactionData);
    }

    @Test
    public void shouldAuthorizeTransaction() throws Exception {
        // Arrange
        List<TransactionResponse> resApproved = new ArrayList<>();
        CardAuthorizationResponse caRes = new CardAuthorizationResponse(
            true, "authorized", "", "", UUID.randomUUID());
        TransactionResponse transactionResponse = new TransactionResponse(transactionData, caRes);
        resApproved.add(transactionResponse);

        // Act
        when(transactionService.createCardAuthorizationResponses(req))
            .thenReturn(resApproved);

        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + AUTHORIZE_TRANSACTION_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(req)));
        
        // Assert
        verify(transactionService).createCardAuthorizationResponses(req);
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].transactionID").value(transactionID.toString()))
            .andExpect(jsonPath("$[0].resData.authorized").value(true))
            .andExpect(jsonPath("$[0].resData.authorizationCode").value("authorized"))
            .andExpect(jsonPath("$[0].resData.declineReason").value(""))
            .andExpect(jsonPath("$[0].resData.bankCallbackUrl").value(""));
    }

    @Test
    public void shouldDeclineTransaction() throws Exception {
        // Arrange
        List<TransactionResponse> resDeclined = new ArrayList<>();
        CardAuthorizationResponse caRes = new CardAuthorizationResponse(
            false, "", "OTP Required.", "bank_callback_url", UUID.randomUUID());
        TransactionResponse transactionResponse = new TransactionResponse(transactionData, caRes);
        resDeclined.add(transactionResponse);

        // Act
        when(transactionService.createCardAuthorizationResponses(req))
            .thenReturn(resDeclined);

        ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + AUTHORIZE_TRANSACTION_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objMapper.writeValueAsString(req)));

        // Assert
        verify(transactionService).createCardAuthorizationResponses(req);
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].transactionID").value(transactionID.toString()))
            .andExpect(jsonPath("$[0].resData.authorized").value(false))
            .andExpect(jsonPath("$[0].resData.authorizationCode").value(""))
            .andExpect(jsonPath("$[0].resData.declineReason").value("OTP Required."))
            .andExpect(jsonPath("$[0].resData.bankCallbackUrl").value("bank_callback_url"));
    }

    // NEED TO REFACTOR BECAUSE VerifyOTPResponse IS DELETED AND CONTROLLER RETURNS TRANSACTIONRESPONSE
    // @Test
    // public void shouldAcceptOTP() throws Exception {
    //     // Arrange
    //     List<UUID> transactionIDs = new ArrayList<>();
    //     transactionIDs.add(transactionID);
    //     TransactionResponse resAccepted = new TransactionResponse(transactionIDs, transactionData, "authorized");

    //     // Act
    //     when(otpValidatorService.validateOTP(any(), any()))
    //         .thenReturn(resAccepted);
        
    //     ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + VERIFY_OTP_ENDPOINT)
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(objMapper.writeValueAsString(otpReq)));

    //     // Assert
    //     verify(otpValidatorService).validateOTP(transactionID, "123456");
    //     result.andExpect(status().isOk());
    //     result.andExpect(jsonPath("$.verified").value(true));
    //     result.andExpect(jsonPath("$.authorizationCode").value("authorized"));
    // }

    // @Test
    // public void shouldDeclineOTP() throws Exception {
    //     // Arrange
    //     List<UUID> transactionIDs = new ArrayList<>();
    //     transactionIDs.add(transactionID);
    //     VerifyOTPResponse resDeclined = new VerifyOTPResponse(transactionIDs, false, "");

    //     // Act
    //     when(otpValidatorService.validateOTP(any(), any()))
    //         .thenReturn(resDeclined);

    //     ResultActions result = mockMvc.perform(post(REQUEST_MAPPING_ENDPOINT + VERIFY_OTP_ENDPOINT)
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .content(objMapper.writeValueAsString(otpReq)));

    //     // Assert
    //     verify(otpValidatorService).validateOTP(transactionID, "123456");
    //     result.andExpect(status().isUnauthorized());
    //     result.andExpect(jsonPath("$.verified").value(false));
    //     result.andExpect(jsonPath("$.authorizationCode").value(""));
    // }
    
}
