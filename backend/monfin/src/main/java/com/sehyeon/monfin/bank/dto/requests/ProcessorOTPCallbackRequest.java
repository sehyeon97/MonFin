package com.sehyeon.monfin.bank.dto.requests;

import java.util.List;
import java.util.UUID;

public record ProcessorOTPCallbackRequest(List<UUID> transactionIDs, String status, String authorizationCode) {
    
}
