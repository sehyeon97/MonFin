package com.sehyeon.monfin.tsp.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.dto.responses.ValidateCardResponse;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.CardRepository;
import com.sehyeon.monfin.bank.services.payment.ValidateCardService;

@ExtendWith(MockitoExtension.class)
public class ValidateCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private ValidateCardService validator;

    private static final String PAN = "1234123412341234";
    private static final String CVV = "456";
    private static final String FULL_NAME = "poo ding";
    private static final String EXP_MONTH = "6";
    private static final String EXP_YEAR = "2031";
    
    private static final BasicCardInfo INFO = new BasicCardInfo(PAN, EXP_MONTH, EXP_YEAR, FULL_NAME, CVV);
    private static final CardType CARD_TYPE = CardType.DEBIT;
    private static final CardNetwork CARD_NETWORK = CardNetwork.VISA;
    private static final CardTier CARD_TIER = CardTier.SILVER;

    // happy path
    @Test
    public void shouldReturnValid() {
        // Arrange
        CardTokenizationRequest req = new CardTokenizationRequest(PAN, CVV, FULL_NAME, EXP_MONTH, EXP_YEAR);
        when(cardRepository.findCardByBasicCardInfo(any()))
            .thenReturn(Optional.of(new Card(0, 0, INFO, CARD_TYPE, CARD_NETWORK, CARD_TIER, Instant.now(), 500, 1000)));

        // Act
        ValidateCardResponse res = validator.doesCardExist(req);
        boolean isValid = res.isValid();
        Card card = res.card();

        // Assert
        assertTrue(isValid);
        assertThat(card.getBasicCardInfo() == INFO);
        assertThat(card.getBalance() == 0);
    }

    // sad path
    @Test
    public void shouldReturnInvalid() {
        // Arrange
        CardTokenizationRequest req = new CardTokenizationRequest("1234567812345678", CVV, FULL_NAME, EXP_MONTH, EXP_YEAR);
        when(cardRepository.findCardByBasicCardInfo(any()))
            .thenReturn(Optional.empty());
        
        // Act
        ValidateCardResponse res = validator.doesCardExist(req);
        
        // Assert
        assertFalse(res.isValid());
    }
    
}
