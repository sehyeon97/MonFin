package com.sehyeon.monfin.bank.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;

/**
 * Spring Security expects a UserDetails because it knows
 * nothing about entities and only understands UserDetails
 * BankAccountDetails
 * Because this will always be an authenticated bank account,
 * every call to a bank account will call this instead of the entity
 */
public class BankAccountDetails implements UserDetails {

    private final BankAccount bankAccount;

    /**
     * This tells us whether we should continue to use the
     * authenticated bank account data or query the database
     * When BankAccountDetails remains unchanged,
     * a database call is unnecessary
     * When the user changes a field, then a query is necessary
     * The code will be cleaner to always query,
     * but I want to avoid making database calls despite using Redis anyway
     * Another way to go about it is just return bank account ID (future consideration) 
     */
    private boolean haveDetailsChanged;

    public BankAccountDetails(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        this.haveDetailsChanged = false;
    }

    /**
     * Empty list means that this account doesn't come with specific roles
     * Both Merchant and Customer are still a bank account in the end
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return bankAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return bankAccount.getUsername();
    }

    public UUID getBankAccountID() {
        return bankAccount.getBankAccountID();
    }

    public String getPhoneNumber() {
        return bankAccount.getPhoneNumber();
    }

    public List<Card> getCards() {
        return bankAccount.getAllCards();
    }

    public void setDetailsHaveChanged() {
        haveDetailsChanged = true;
    }

    public boolean haveDetailsChanged() {
        return haveDetailsChanged;
    }
    
}
