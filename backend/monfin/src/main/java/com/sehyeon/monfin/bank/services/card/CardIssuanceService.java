package com.sehyeon.monfin.bank.services.card;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.exceptions.IncorrectFPEConfig;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.limits.CardLimits;
import com.sehyeon.monfin.bank.model.limits.SpendingLimits;

@Service
public class CardIssuanceService {    
    @Autowired
    private CardTokenMaker cardTokenMaker;

    public CardIssuanceService() {
        
    }

    // At the moment, only creates DEBIT cards
    public Card issueCard(
        BankAccount bankAccount, String fullName, CardType cardType, CardNetwork cardNetwork, CardTier cardTier) {
        // look up card tier based on card network and assign daily and monthly limits
        // TODO: works for credit cards only for now (make it work with debit)
        CardLimits cardLimits = new CardLimits();
        SpendingLimits spendingLimits = cardLimits.getSpendingLimits(cardNetwork, cardTier);

        // call CardTokenService.java to generate token + last4 + expire date
        String encryptedCardNumber = generateTokenizedPAN(cardNetwork);
        
        // create a new Card based on the given and evaluated data
        LocalDate today = LocalDate.now();
        int expYear = today.getYear() + 5;
        String securityCode = generateSecurityCode();
        BasicCardInfo basicCardInfo = new BasicCardInfo(encryptedCardNumber, Integer.toString(today.getMonthValue()), Integer.toString(expYear), fullName, securityCode);
        Card card = new Card(
            0, 0, basicCardInfo,
            cardType, cardNetwork, cardTier, Instant.now(),
            spendingLimits.getDailyAmountLimit(), spendingLimits.getMonthlyAmountLimit()
        );

        // Save card to "cards" table
        // The card table has bank account id as foreign key so that the bank account table doesn't need to store extra props
        // Also saves card amount info
        // This is where the encrypted card number is also stored
        // cardService.createCard(card);
        return card;
    }

    private String generateTokenizedPAN(CardNetwork network) {
        String pan = generatePAN(network);
        String iin = pan.substring(0, 6);
        String middleSixDigits = pan.substring(6, 12);
        if (middleSixDigits.equals("")) {
            throw new IncorrectFPEConfig("Wrong FPE configuration settings");
        }
        String encryptedMiddleSix = cardTokenMaker.encrypt(middleSixDigits);
        String last4 = pan.substring(12);
        return iin + encryptedMiddleSix + last4;
    }

    private String generatePAN(CardNetwork network) {
        Random randomNumber = new Random();
        StringBuilder pan = new StringBuilder();

        switch (network) {
            case VISA:
                pan.append(4);
                break;
            case MASTERCARD:
                pan.append(5);
                break;
            case DISCOVER:
                pan.append(6);
                break;
            default:
                pan.append(9);
                break;
        }

        for (int i = 0; i < 15; i++) {
            pan.append(randomNumber.nextInt(10));
        }

        return pan.toString();
    }

    private String generateSecurityCode() {
        StringBuilder securityCode = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            securityCode.append(random.nextInt(10));
        }

        return securityCode.toString();
    }

}
