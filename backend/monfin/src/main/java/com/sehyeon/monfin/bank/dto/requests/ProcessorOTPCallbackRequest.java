package com.sehyeon.monfin.bank.dto.requests;

import java.util.UUID;

public record ProcessorOTPCallbackRequest(UUID transactionID, String status, String authorizationCode) {
    
}
