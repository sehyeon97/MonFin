package com.sehyeon.monfin.bank.services.payment;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.dto.responses.ValidateCardResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.CardRepository;

/**
 * When Payment Processor sends card to get a token,
 * we need to first identify that the card exists with the bank
 * if not, we don't create a token
 */
@Service
public class ValidateCardService {

    @Autowired
    private CardRepository cardRepository;

    public ValidateCardService() {}

    public ValidateCardResponse doesCardExist(CardTokenizationRequest request) {
        BasicCardInfo info = new BasicCardInfo(request.pan(), request.expMonth(), request.expYear(), request.fullName(), request.cvv());
        Optional<Card> card = cardRepository.findCardByBasicCardInfo(info);

        if (card.isPresent()) {
            return new ValidateCardResponse(true, card.get());
        }

        return new ValidateCardResponse(false, null);
    }
    
}
