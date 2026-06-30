package com.sehyeon.monfin.bank.model.entity.bank;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID transactionID;

    // Which card made the transaction
    private UUID cardID;

    // Which bank account the card belongs to
    private UUID bankAccountID;

    // just in case
    private String cardToken;

    // The date at which the transaction occurred
    private String day;
    private String month;
    private String year;

    // Merchant information
    private String merchantName;
    private UUID merchantID;

    private int amount;
    private Instant timestamp;

    // whether the Transaction was "Approved", "Pending", or "Declined"
    // Later, the Pending result would be set to Approved
    // It cannot be approved immediately <--- this is part of MVP
    // unless it's prepaid or zelle. <--- this one is not part of the MVP
    private String result;

    protected Transaction() {}

    public Transaction(
        UUID transactionID, UUID cardID, UUID bankAccountID, String cardToken,
        String day, String month, String year,
        String merchantName, UUID merchantID,
        int amount, Instant timestamp, String result
    ) {
        this.transactionID = transactionID;
        this.cardID = cardID;
        this.bankAccountID = bankAccountID;
        this.cardToken = cardToken;
        this.day = day;
        this.month = month;
        this.year = year;
        this.merchantName = merchantName;
        this.merchantID = merchantID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.result = result;
    }

    public UUID getID() {
        return transactionID;
    }

    public void setResultToPending() {
        result = "Pending";
    }

    public void setResultToApproved() {
        if (result.equalsIgnoreCase("pending")) {
            result = "Approved";
        }
    }

    public String getResult() {
        return result;
    }

    public UUID getCardID() {
        return cardID;
    }

    public UUID getBankAccountID() {
        return bankAccountID;
    }

    public String getCardToken() {
        return cardToken;
    }

    public String getDate() {
        return String.format(
            "%02d",
            Integer.parseInt(month)) + "/" + String.format("%02d", Integer.parseInt(day)) + "/" + year;
    }

    public String getMerchantName() {
        return merchantName;
    }

    // probably won't be called, but it will be stored in database just in case
    public UUID getMerchantID() {
        return merchantID;
    }

    public String getAmount() {
        return Integer.toString(amount);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTransactionStatus() {
        return result;
    }
    
}
