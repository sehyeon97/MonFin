package com.sehyeon.monfin.bank.services.bank;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccountInbox;
import com.sehyeon.monfin.bank.model.entity.bank.Transaction;
import com.sehyeon.monfin.bank.repos.BankAccountInboxRepository;

@ExtendWith(MockitoExtension.class)
public class BankInboxServiceTest {

    @Mock
    private BankAccountInboxRepository inboxRepo;

    private static final String ONE_TIME_PASSCODE = "123456";

    @InjectMocks
    private BankInboxService inboxService;

    @Test
    @Rollback
    public void shouldSendOTPToInbox() {
        // Arrange
        UUID bankAccountID = UUID.randomUUID();

        // Act
        inboxService.sendOTP(bankAccountID, ONE_TIME_PASSCODE);

        // Assert
        verify(inboxRepo).save(any(BankAccountInbox.class));
    }

    @Test
    @Rollback
    public void shouldSendCardStatusChangesToInbox() {
        // Arrange
        Transaction transaction = new Transaction(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID().toString(),
            "30", "6", "2026", "Amazon", UUID.randomUUID(),
            99, Instant.now(), "APPROVED");

        // Act
        inboxService.sendCardStatus(UUID.randomUUID(), "ACTIVE", transaction);

        // Assert
        verify(inboxRepo).save(any(BankAccountInbox.class));
    }
    
}
