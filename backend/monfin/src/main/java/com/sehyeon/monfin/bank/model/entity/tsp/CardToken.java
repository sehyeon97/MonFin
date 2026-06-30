package com.sehyeon.monfin.bank.model.entity.tsp;

import java.util.UUID;

import com.sehyeon.monfin.bank.model.entity.bank.Card;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens")
public class CardToken {
    
    @Id
    private String cardToken;

    // foreign key
    // many tokens map to one card
    @ManyToOne
    @JoinColumn(name = "id") // the card id
    private Card card; // the name is important bc of how it's used in card.java "mappedBy"

    protected CardToken() {}

    public CardToken(String cardToken, Card card) {
        this.cardToken = cardToken;
        this.card = card;
        card.getTokens().add(this);
    }

    public String getCardToken() {
        return cardToken;
    }

    public UUID getCardID() {
        return card.getCardID();
    }

}
