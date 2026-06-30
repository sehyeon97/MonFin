package com.sehyeon.monfin.bank.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.bank.OneTimePasscode;

@Repository
public interface OTPRepository extends JpaRepository<OneTimePasscode, UUID> {

    public Optional<OneTimePasscode> findByTransactionID(UUID transactionID);
    
}
