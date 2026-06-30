package com.sehyeon.monfin.bank.repos;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.bank.Card;

@Repository
public interface UserCardRepository extends JpaRepository<Card, UUID> {

    public Card findCardByCardID(UUID cardID);
    
}
