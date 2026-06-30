package com.sehyeon.monfin.bank.model.entity.bank;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Shortened to OTP
@Entity
@Table(name = "otp")
public class OneTimePasscode {

    @Id
    @GeneratedValue
    private UUID otpID;

    private UUID transactionID;
    private String otp;
    private String ppCallbackUrl;

    protected OneTimePasscode() {}

    public OneTimePasscode(UUID transactionID, String otp, String ppCallbackUrl) {
        this.transactionID = transactionID;
        this.otp = otp;
        this.ppCallbackUrl = ppCallbackUrl;
    }

    public UUID getOtpID() {
        return otpID;
    }

    public UUID getTransactionID() {
        return transactionID;
    }

    public String getOTP() {
        return otp;
    }

    public String getPPCallbackUrl() {
        return ppCallbackUrl;
    }
    
}
