package com.sehyeon.monfin.bank.model.entity.transactions;

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

    // private UUID transactionID; Refactored to new Entity called TransactionOTP
    private String otp;
    private String redirectUrl;
    private String serverUrl;

    protected OneTimePasscode() {}

    public OneTimePasscode(String otp, String redirectUrl, String serverUrl) {
        this.otp = otp;
        this.redirectUrl = redirectUrl;
        this.serverUrl = serverUrl;
    }

    public UUID getOtpID() {
        return otpID;
    }

    public String getOTP() {
        return otp;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }
    
}
