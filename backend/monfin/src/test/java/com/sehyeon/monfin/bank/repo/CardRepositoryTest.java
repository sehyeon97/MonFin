package com.sehyeon.monfin.bank.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.CardRepository;

import jakarta.transaction.Transactional;

// Testing to see if it returns a Card given that BasicCardInfo is not an entity
// directly associated with this repository
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach()
    public void init() {
        cardRepository.deleteAll();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void doesBasicInfoRetrieveCardSuccessfully() {
        // Here, the encrypted card number equals PAN for testing simplicity
        String PAN = "1234123412341234";
        String expMonth = "12";
        String expYear = "2026";
        String fullName = "pooding";
        String securityCode = "123";
        
        BasicCardInfo basicCardInfo = new BasicCardInfo(PAN, expMonth, expYear, fullName, securityCode);
        Card card = new Card(
            0, 5000, basicCardInfo, CardType.CREDIT, CardNetwork.VISA, CardTier.SILVER,
            Instant.now(), 1000, 5000
        );

        cardRepository.save(card);


        Optional<Card> cardFromDB = cardRepository.findCardByBasicCardInfo(basicCardInfo);

        // assertThat(cardFromDB.isPresent()) would make sure it returned an optional
        // hence the above line is pretty useless
        // the below line makes sure that the optional card actually holds a value and isn't empty
        assertThat(cardFromDB).isPresent();
    }
    
} // BasicCardInfo has to be part of Card.java for this to work
