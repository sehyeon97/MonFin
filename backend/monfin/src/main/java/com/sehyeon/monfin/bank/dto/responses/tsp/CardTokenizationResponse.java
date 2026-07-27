package com.sehyeon.monfin.bank.dto.responses.tsp;

import com.sehyeon.monfin.bank.model.card.network.CardNetwork;

public record CardTokenizationResponse(boolean tokenized, String message, String cardToken, CardNetwork network) {
    
}
