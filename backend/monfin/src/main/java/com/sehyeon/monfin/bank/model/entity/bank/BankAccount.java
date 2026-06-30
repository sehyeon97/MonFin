package com.sehyeon.monfin.bank.model.entity.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue
    private UUID bankAccountID;
    private String username;
    private String password;
    private String fullname;
    private String phoneNumber;

    // a bank account can hold multiple cards
    @OneToMany(mappedBy = "bankAccount")
    private List<Card> cards;

    // REQUIRED BY JPA
    // WITHOUT A DEFAULT CONSTRUCTOR, TESTS WILL RETURN INTERNAL SERVER ERROR 500 instead of OK 200
    // Uses PROTECTED so I don't accidentally use this constructor outside of this class
    protected BankAccount() {}

    public BankAccount(String username, String password, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullname = fullName;
        this.cards = new ArrayList<>();
    }

    public UUID getBankAccountID() {
        return bankAccountID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullname;
    }

    public void addCard(Card card) {
        cards.add(card);
        card.setBankAccount(this);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
