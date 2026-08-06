package com.sehyeon.monfin.bank.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;

/**
 * Part of Spring Security *
 * This is automatically called by the Authentication Manager
 * BankAccountDetailsService should never be called directly
 */
@Service
public class BankAccountDetailsService implements UserDetailsService {

    private final BankRepository bankRepository;

    public BankAccountDetailsService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    // interface method name. However, the project loads Bank Account
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankAccount bankAccount = bankRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));

        // Spring Security authenticates using a class that implements UserDetails, not the Entity
        // Later, will need to add Redis to cache database queries because this will only return
        return new BankAccountDetails(bankAccount);
    }

    public BankAccountDetails loadBankAccountByID(UUID bankAccountID) throws UsernameNotFoundException {
        BankAccount bankAccount = bankRepository.findById(bankAccountID)
                            .orElseThrow(() -> new UsernameNotFoundException("Invalid ID"));

        return new BankAccountDetails(bankAccount);
    }
    
}
