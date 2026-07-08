package com.sehyeon.monfin.bank.model.entity.transactions;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * This entity shows the relationship between a transaction that requires an otp
 * to the otp they are assigned to
 * It's useful when multiple transactions require an otp in one checkout
 * Instead of sending OTPs multiple times, we can send the customer one OTP
 */
@Entity
@Table(name = "transaction_otp")
public class TransactionOTP {

    @Id
    @GeneratedValue
    private UUID transactionOtpID;

    private UUID otpID;
    private UUID transactionID;

    protected TransactionOTP() {}

    public TransactionOTP(UUID otpID, UUID transactionID) {
        this.otpID = otpID;
        this.transactionID = transactionID;
    }

    public UUID getOtpId() {
        return otpID;
    }

    public UUID getTransactionId() {
        return transactionID;
    }
    
}
