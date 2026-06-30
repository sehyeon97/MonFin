package com.sehyeon.monfin.bank.model.card.basic;

import jakarta.persistence.Embeddable;

@Embeddable
public class BasicCardInfo {

    private String PAN;
    private String expMonth;
    private String expYear;
    private String fullName;
    private String securityCode;

    // Required to have as an @Embeddable
    protected BasicCardInfo() {}

    public BasicCardInfo(String PAN, String expMonth, String expYear, String fullName, String securityCode) {
        this.PAN = PAN;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.fullName = fullName;
        this.securityCode = securityCode;
    }

    public String getPAN() {
        return PAN;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    
}
