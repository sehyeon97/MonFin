package com.sehyeon.monfin.bank.services.bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.services.card.CardIssuanceService;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest {
    
    @Mock
    private BankRepository bankRepository;

    @Mock
    private CardService cardService;

    @Mock
    private CardIssuanceService cardIssuanceService;

    @InjectMocks
    private BankAccountService bankAccountService;

    @Test
    public void shouldIssueAndCreateCard() {
    }

}
