package com.sehyeon.monfin.bank.dto.responses;

public record CardTokenizationResponse(boolean tokenized, String message, String cardToken) {
    
}
