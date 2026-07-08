package com.sehyeon.monfin.bank.repos.transactions;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sehyeon.monfin.bank.model.entity.transactions.OneTimePasscode;

@Repository
public interface OTPRepository extends JpaRepository<OneTimePasscode, UUID> {
    
}
