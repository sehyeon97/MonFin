package com.sehyeon.monfin.tsp.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.model.payment.TokenizedCardInfo;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;
import com.sehyeon.monfin.bank.services.payment.PPCardTokenizer;

// Service layer testing usually is done using ExtendWith MockitoExtension
// It isolates the specific service, making the tests fast
// Service layer testing tries to avoid loading up the full spring boot context (No @SpringBootTest)
@ExtendWith(MockitoExtension.class)
public class PPCardTokenizerTest {

    @Mock
    private CardTokenRepository cardTokenDB;

    @InjectMocks
    private PPCardTokenizer tokenizer;

    @Test
    public void shouldTokenizeCard() {
        // Arrange
        String pan = "1234123412341234";
        String cvv = "123";
        String fullName = "pooding";
        String expMonth = "6";
        String expYear = "2031";
        String merchantID = "11509964";
        CardTokenizationRequest req = new CardTokenizationRequest(pan, cvv, fullName, expMonth, expYear);

        CardType cardType = CardType.DEBIT;
        CardNetwork cardNetwork = CardNetwork.VISA;
        CardTier cardTier = CardTier.SILVER;
        BasicCardInfo basicCardInfo = new BasicCardInfo(pan, expMonth, expYear, fullName, merchantID);
        Card card = new Card(0, 0, basicCardInfo, cardType, cardNetwork, cardTier, Instant.now(), 500, 1000);

        // Act
        TokenizedCardInfo tokenizedInfo = tokenizer.generateCardToken(req, card);
        String tokenizedCardDetails = tokenizedInfo.getCardDetailsForPP();
        String[] details = tokenizedCardDetails.split("\\|");

        // to test that token is NOT deterministic based on SAME parameter values
        TokenizedCardInfo otherTokenizedInfo = tokenizer.generateCardToken(req, card);
        String[] otherDetails = otherTokenizedInfo.getCardDetailsForPP().split("\\|");

        // Assert
        // card token | last4 | expire month | expire year
        for (String detail : details) {
            System.out.println("Detail: " + detail);
        }
        assertTrue(details.length == 4);

        // card token
        assertThat(details[0]).isNotBlank();
        System.out.println("Detail for card token: " + details[0]);
        System.out.println("Detail for OTHER card token: " + otherDetails[0]);
        assertTrue(!details[0].equalsIgnoreCase(otherDetails[0]));

        // last4
        assertTrue(details[1].length() == 4);
        assertTrue(details[1].equalsIgnoreCase(otherDetails[1]));

        // expire month & year
        assertThat(details[2].equalsIgnoreCase(otherDetails[2])).isTrue();
        assertThat(details[2].equalsIgnoreCase("6")).isTrue();
        assertThat(details[3].equalsIgnoreCase(otherDetails[3])).isTrue();
        assertThat(details[3].equalsIgnoreCase("2031")).isTrue();

        // bonus: checking if the tokens are stored in card's list of tokens
        List<CardToken> tokens = card.getTokens();
        assertTrue(tokens.size() == 2);
        assertTrue(tokens.get(0).getCardToken().equalsIgnoreCase(details[0]));
        assertTrue(tokens.get(1).getCardToken().equalsIgnoreCase(otherDetails[0]));
    }
    
}
