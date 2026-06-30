package com.sehyeon.monfin.bank.services.bank;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.CardRepository;

import jakarta.transaction.Transactional;

/**
 * Approves or rejects charges
 * Retrieves amount of funds or balances
 */
@Service
public class CardService {
    
    @Autowired
    private CardRepository cardRepository;

    public CardService() {}

    @Transactional
    public void createCard(Card card) {
        cardRepository.save(card);
    }

    @Transactional
    public boolean chargeCard(Card card, int amount) {
        // getReferenceById assumes the card ID exists
        Card cardDetails = cardRepository.getReferenceById(card.getCardID());
        int cardAmount = cardDetails.getBalance();

        // Entity is managed by JPA and the field is updated in memory
        // At the end of the Transactional method, the data gets updated
        if (cardAmount >= amount) {
            // approve charge and deduct amount from card
            cardAmount -= amount;
            cardDetails.setBalance(cardAmount);
            return true;
        }

        // reject charge
        return false;
    }

    @Transactional
    public void receivePayment(UUID bankAccountID) {
        // Optional<Card> card = cardRepository.findCardByBankAccount_BankAccountID(bankAccountID);
        
    }

    public Optional<Card> getCardByID(UUID cardID) {
        return cardRepository.findById(cardID);
    }

}
