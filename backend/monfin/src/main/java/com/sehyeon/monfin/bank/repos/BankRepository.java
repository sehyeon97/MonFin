package com.sehyeon.monfin.bank.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;

/**
 * Handle various database interactions with bank_accounts
 * Assumes one user = one bank for now. Later, add one user = many bank accounts
 */
@Repository
public interface BankRepository extends JpaRepository<BankAccount, UUID> {

    // return bank account ID based on bank account login credentials
    // this is not secure, but it's an MVP
    public Optional<BankAccount> findByUsernameAndPassword(String username, String password);
    
}
