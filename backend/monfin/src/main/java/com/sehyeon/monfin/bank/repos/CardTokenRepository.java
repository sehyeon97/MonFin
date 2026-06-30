package com.sehyeon.monfin.bank.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;

@Repository
public interface CardTokenRepository extends JpaRepository<CardToken, String>{

    public Optional<CardToken> findByCardToken(String cardToken);
    
}
