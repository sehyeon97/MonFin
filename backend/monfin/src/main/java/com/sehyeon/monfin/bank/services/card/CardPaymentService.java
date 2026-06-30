package com.sehyeon.monfin.bank.services.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * For Merchants when their card is about to increase in balance (must be for debit cards)
 */
@Service
public class CardPaymentService {

    // @Autowired
    // private CardService cardService;

    @Autowired

    public CardPaymentService() {
        
    }

    public void updateCard(String merchantID) {}
    
}
