package com.sehyeon.monfin.bank.model.entity.bank;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inbox")
public class BankAccountInbox {

    @Id
    @GeneratedValue
    private UUID inboxID;

    // which bank account this inbox is for
    private UUID bankAccountID;

    private String title;
    private String message;
    private Instant timestamp;
    
    protected BankAccountInbox() {}

    public BankAccountInbox(UUID bankAccountID, String title, String message) {
        this.bankAccountID = bankAccountID;
        this.title = title;
        this.message = message;
        timestamp = Instant.now();
    }

    public UUID getBankAccountID() {
        return bankAccountID;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    
}
