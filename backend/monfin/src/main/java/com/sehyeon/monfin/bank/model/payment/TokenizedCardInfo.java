package com.sehyeon.monfin.bank.model.payment;

public class TokenizedCardInfo {

    private final String cardToken;
    private final String last4;
    private final String expMonth;
    private final String expYear;

    public TokenizedCardInfo(String cardToken, String last4, String expMonth, String expYear) {
        this.cardToken = cardToken;
        this.last4 = last4;
        this.expMonth = expMonth;
        this.expYear = expYear;
    }

    // will need to .split() by "|" later
    public String getCardDetailsForPP() {
        return cardToken + "|" + last4 + "|" + expMonth + "|" + expYear;
    }
    
}
