package com.sehyeon.monfin.bank.dto.requests;

import java.util.UUID;

public record NewCardRequest(UUID bankAccountID, String cardType, String cardNetwork, String cardTier) {
    
}
