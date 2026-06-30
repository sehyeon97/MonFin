package com.sehyeon.monfin.bank.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.entity.bank.Card;

// payment platform would hit this eventually
// select Card where encryptedNum, security code, user's full name, and exp month/year matches
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    // Method name must include "BasicCardInfo"
    // For example, if it was "findCardByBasicInfo",
    // the call will fail because "basicInfo" cannot be found
    public Optional<Card> findCardByBasicCardInfo(BasicCardInfo basicCardInfo);

    // For getting card associated with MERCHANT
    public Optional<Card> findCardByBankAccount_BankAccountID(UUID bankAccountID);
    
}
