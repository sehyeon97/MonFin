package com.sehyeon.monfin.bank.repos.transactions;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sehyeon.monfin.bank.model.entity.transactions.TransactionOTP;

public interface TransactionOTPRepository extends JpaRepository<TransactionOTP, UUID> {

    public List<TransactionOTP> findAllByOtpID(UUID otpID);
    
}
