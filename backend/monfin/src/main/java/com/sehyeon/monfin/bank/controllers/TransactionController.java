package com.sehyeon.monfin.bank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.requests.VerifyOTPRequest;
import com.sehyeon.monfin.bank.dto.responses.CardAuthorizationResponse;
import com.sehyeon.monfin.bank.dto.responses.VerifyOTPResponse;
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
    public ResponseEntity<String> authorizeTransaction(@Valid @RequestBody CardAuthorizationRequest req) {
        CardAuthorizationResponse res = transactionService.createCardAuthorizationResponse(req);
        return res.authorized() ? ResponseEntity.ok(res.authorizationCode()) 
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res.declineReason());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOTPResponse> verifyOTP(@RequestBody VerifyOTPRequest req) {
        VerifyOTPResponse res = otpValidatorService.validateOTP(req.transactionID(), req.otp());
        return res.verified() ? ResponseEntity.ok(res) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }
    
}
