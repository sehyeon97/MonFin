package com.sehyeon.monfin.bank.services.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.dto.responses.CardTokenizationResponse;
import com.sehyeon.monfin.bank.dto.responses.ValidateCardResponse;
import com.sehyeon.monfin.bank.model.payment.TokenizedCardInfo;

@Service
public class TokenServiceProvider {
    
    @Autowired
    private PPCardTokenizer cardTokenizer;

    @Autowired
    private ValidateCardService cardValidator;

    public TokenServiceProvider() {}

    public CardTokenizationResponse tokenizeCard(CardTokenizationRequest request) {
        // first, verify card is valid
        ValidateCardResponse card = cardValidator.doesCardExist(request);
        if (card.isValid()) {
            TokenizedCardInfo cardInfo = cardTokenizer.generateCardToken(request, card.card());
            return new CardTokenizationResponse(true, "Tokenization Successful", cardInfo.getCardDetailsForPP());
        }

        return new CardTokenizationResponse(false, "Tokenization Unsuccessful", "");
    }

}
