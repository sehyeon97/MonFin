package com.sehyeon.monfin.bank.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.requests.VerifyOTPRequest;
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
import com.sehyeon.monfin.bank.services.transactions.OTPValidatorService;
import com.sehyeon.monfin.bank.services.transactions.TransactionService;

import jakarta.validation.Valid;

/**
 * Handles transactions requested by payment processor for customer
 */
@RestController
@RequestMapping("/api/bank/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OTPValidatorService otpValidatorService;

    public TransactionController() {}

    @PostMapping("/authorize")
    public ResponseEntity<List<TransactionResponse>> authorizeTransaction(@Valid @RequestBody List<CardAuthorizationRequest> req) {
        List<TransactionResponse> res = transactionService.createCardAuthorizationResponses(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<List<TransactionResponse>> verifyOTP(@RequestBody VerifyOTPRequest req) {
        List<TransactionResponse> res = otpValidatorService.validateOTP(req.otpID(), req.otp(), req.metaData());
        return ResponseEntity.ok(res);
    }
    
}
