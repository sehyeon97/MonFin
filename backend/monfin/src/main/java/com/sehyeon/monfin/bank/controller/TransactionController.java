package com.sehyeon.monfin.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CardAuthorizationRequest;
import com.sehyeon.monfin.bank.dto.requests.VerifyOTPRequest;
import com.sehyeon.monfin.bank.dto.responses.transaction.BankTransactionRecord;
import com.sehyeon.monfin.bank.dto.responses.transaction.TransactionResponse;
import com.sehyeon.monfin.bank.security.BankAccountDetails;
import com.sehyeon.monfin.bank.services.transactions.OTPValidatorService;
import com.sehyeon.monfin.bank.services.transactions.TransactionService;

import jakarta.validation.Valid;

/**
 * Handles transactions requested by payment processor for customer
 * Future Note: Incorporate Web Sockets to emit events to frontend on new transaction results
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

    @GetMapping("/get-transactions")
    public ResponseEntity<List<BankTransactionRecord>> getTransactions(Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        return ResponseEntity.ok(transactionService.getAllTransactions(bankAccount.getBankAccountID()));
    }
    
}
