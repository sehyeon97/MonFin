package com.sehyeon.monfin.bank.services.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.responses.card.AccountCardsResponse;
import com.sehyeon.monfin.bank.dto.responses.card.BasicCardInfoResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
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
    public void removeCard(UUID bankAccountID, String last4) {
        cardRepository.deleteByBankAccount_BankAccountIDAndLastFour(bankAccountID, last4);
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

    // should try to return as many details about card as possible
    // so that any future calls will already have the details needed
    public AccountCardsResponse getCardsOwnedByAccount(UUID bankAccountID) {
        List<Card> cards = cardRepository.findAllByBankAccount_BankAccountID(bankAccountID);
        List<BasicCardInfoResponse> infos = new ArrayList<>();

        for (Card card : cards) {
            BasicCardInfo info = card.getBasicCardInfo();
            BasicCardInfoResponse response = new BasicCardInfoResponse(
                card.getLastFour(),
                info.getExpMonth(),
                info.getExpYear(),
                card.getCardTier().toString(),
                card.getCardNetwork().toString(),
                card.getCardType().toString(),
                card.getCardStatus().toString(),
                card.getMonthlyLimit(),
                card.getDailyLimit(),
                card.getAvailableCredit(),
                card.getBalance(),
                card.isDebit()
            );
            infos.add(response);
        }
        
        return new AccountCardsResponse(infos);
    }

}
