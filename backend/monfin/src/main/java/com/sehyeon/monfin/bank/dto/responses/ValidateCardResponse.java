package com.sehyeon.monfin.bank.dto.responses;

import com.sehyeon.monfin.bank.model.entity.bank.Card;

/**
 * If card is valid, it will be the string form of the card uuid
 * If card is not found / invalid, it will say "INVALID"
 */
public record ValidateCardResponse(boolean isValid, Card card) {
    
}
