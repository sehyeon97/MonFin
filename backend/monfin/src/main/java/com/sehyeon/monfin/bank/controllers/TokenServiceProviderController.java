package com.sehyeon.monfin.bank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.dto.responses.CardTokenizationResponse;
import com.sehyeon.monfin.bank.services.payment.TokenServiceProvider;

import jakarta.validation.Valid;

// When payment trasactions are CREDIT / DEBIT, it must go through this mapping
@RestController
@RequestMapping("/api/bank/payment/processor")
public class TokenServiceProviderController {

    @Autowired
    private TokenServiceProvider tsp; // service

    public TokenServiceProviderController(TokenServiceProvider tsp) {
        this.tsp = tsp;
    }

    /**
     * The tokenization of the card which will be returned to the payment processor is not
     * the same token as the token created during card issuance. That one is core banking fundamental,
     * whereas this is related to Token Service Provider (response) and Payment Processing (request)
     */
    @PostMapping("/credentials")
    public ResponseEntity<CardTokenizationResponse> tokenizeCard(@Valid @RequestBody CardTokenizationRequest request) {
        CardTokenizationResponse response = tsp.tokenizeCard(request);
        if (response.tokenized()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
}
