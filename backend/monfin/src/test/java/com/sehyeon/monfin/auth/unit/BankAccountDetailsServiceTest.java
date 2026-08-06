package com.sehyeon.monfin.auth.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.security.BankAccountDetails;
import com.sehyeon.monfin.bank.security.BankAccountDetailsService;

@ExtendWith(MockitoExtension.class)
public class BankAccountDetailsServiceTest {

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankAccountDetailsService bankAccountDetailsService;

    @Test
    public void shouldRetrieveBankAccountDetailsByBankAccountId() {
        // Arrange
        BankAccount expectedBankAccount = new BankAccount(
            "sehyeoo",
            "encrypted",
            "really",
            "909-951-0000"
        );
        UUID expectedBankAccountID = expectedBankAccount.getBankAccountID();
        when(bankRepository.findById(expectedBankAccountID))
            .thenReturn(Optional.of(expectedBankAccount));
        
        // Act
        BankAccountDetails bankAccount =
            bankAccountDetailsService.loadBankAccountByID(expectedBankAccountID);

        // Assert
        assertEquals(expectedBankAccountID, bankAccount.getBankAccountID());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionOnInvalidBankAccountId() {
        // Arrange
        UUID invalidBankAccountID = UUID.randomUUID();
        when(bankRepository.findById(invalidBankAccountID))
            .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception =
                assertThrows(
                    UsernameNotFoundException.class,
                    () -> bankAccountDetailsService.loadBankAccountByID(invalidBankAccountID)
                );
        assertEquals("Invalid ID", exception.getMessage());
    }
    
}
