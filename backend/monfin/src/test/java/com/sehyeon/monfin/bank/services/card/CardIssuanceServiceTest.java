package com.sehyeon.monfin.bank.services.card;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.BankRepository;

@SpringBootTest
public class CardIssuanceServiceTest {

    @Autowired
    private CardIssuanceService cardIssuanceService;
    @Autowired
    private BankRepository bankRepository;

    @Test
    public void issueCardCreatesCard() {
        BankAccount bankAccount = new BankAccount("John", "Doe", "John Doe", "9096773328");
        bankRepository.save(bankAccount);

        String fullName = "John Doe";
        Card card = cardIssuanceService.issueCard(bankAccount, fullName, CardType.CREDIT, CardNetwork.VISA, CardTier.SILVER);

        assertNotNull(card);
    }
    
}
