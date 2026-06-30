package com.sehyeon.monfin.bank.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccountInbox;

public interface BankAccountInboxRepository extends JpaRepository<BankAccountInbox, UUID> {

    // list would be empty if no records are found, therefore doesn't need Optional
    public List<BankAccountInbox> findByBankAccountID(UUID bankAccountID);
    
}
