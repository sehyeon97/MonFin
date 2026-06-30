package com.sehyeon.monfin.bank.model.entity.bank;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sehyeon.monfin.bank.model.card.basic.BasicCardInfo;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.status.CardStatus;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * This object defines the properties of a physical card
 * As of now, international transactions will be nonexistent
 */
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID card_id; // Card ID; primary key (this id is unique to each row)

    @ManyToOne
    @JoinColumn(name = "bankAccountID")
    private BankAccount bankAccount; // foreign key (called in relation to bank_accounts)

    // private String encryptedCardNum;         @Transition to BasicCardInfo
    // ||||||||||| DEBIT AND CREDIT USE BALANCE |||||||||||
    private int balance; // in cents | for credit, it would start with a number > 0. for debit, start @ zero.
    // ||||||||||| DEBIT AND CREDIT USE BALANCE |||||||||||

    // ||||||||||| CREDIT ONLY |||||||||||
    private int availableCredit; // always 0 if card type = DEBIT
    // ||||||||||| CREDIT ONLY |||||||||||
    

    // Which bank account this card comes from
    // This is important because when a card is lost or stolen, 
    // the money stays in the bank account so only the card needs replacing
    // private final UUID bankAccountID;

    // Basic Card Info for payments
    @Embedded
    private final BasicCardInfo basicCardInfo;

    private final CardType cardType;
    private CardStatus cardStatus;
    private final CardNetwork cardNetwork;
    private final CardTier cardTier;
    private final String last4;
    // private final int expMonth;              @Transition to BasicCardInfo
    // private final int expYear; // 4 digits   @Transition to BasicCardInfo
    // private final String fullName;           @Transition to BasicCardInfo
    // private final String securityCode;       @Transition to BasicCardInfo
    private final Instant issuedAt;
    private Instant activatedAt;

    // in cents (e.g. 100 cents = 1 dollar / 1000 cents = 10 dollars)
    private int dailyLimit;
    private int monthlyLimit;

    // 0-30: normal | 31-69: need monitoring | 70-100: freeze card
    private int riskScore;

    /** true when 31+, false otherwise
    * When flaggedForReview is true, send the customer an email or text message about the transaction
    * The card is not auto-declined. however, the transaction will undergo a review
    * Review may conclude as "Approved," "Rejected," or "Escalated"
    * Escalated meeans the customer must contact the bank's fraud department to clear fraud suspiscion
    * Rejected and Escalated will both put the card to frozen status
    * Rejected will clear itself in a few days but risk score will increase
    */
    private boolean flaggedForReview;

    // Timestamp of most recent successful transaction
    // Purpose: dormancy detection, first use after long inactivity,
    //          transactions per min/hour/day, UX insights (last used 3 hours ago)
    private Instant lastUsedAt;

    // the number of tokens this card is associated with
    @OneToMany(mappedBy = "card")
    private List<CardToken> tokens;

    // required to test
    protected Card() {
        this.basicCardInfo = null;
        this.cardType = null;
        this.cardNetwork = null;
        this.cardTier = null;
        this.last4 = "";
        this.issuedAt = null;
    }

    public Card(
        int balance, int availableCredit, BasicCardInfo basicCardInfo,
        CardType cardType, CardNetwork cardNetwork, CardTier cardTier,
        Instant issuedAt, int dailyLimit, int monthlyLimit
    ) {
        // this.encryptedCardNum = encryptedCardNum;        @Transition to BasicCardInfo
        this.balance = balance;
        this.availableCredit = availableCredit;
        this.basicCardInfo = basicCardInfo;
        this.cardType = cardType;
        this.cardStatus = CardStatus.ISSUED;
        this.cardNetwork = cardNetwork;
        this.cardTier = cardTier;
        this.last4 = "Idontknowyet"; // handled separately, not here
        // this.expMonth = expMonth;                        @Transition to BasicCardInfo
        // this.expYear = expYear;                          @Transition to BasicCardInfo
        // this.fullName = fullName;                        @Transition to BasicCardInfo
        // this.securityCode = securityCode;                @Transition to BasicCardInfo
        this.issuedAt = issuedAt;
        this.activatedAt = issuedAt; // The user needs to activate, but for now the placeholder is the same as issuedAt
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.riskScore = 0;
        this.flaggedForReview = false;
        this.lastUsedAt = activatedAt; // Whenever the user uses the card, this value changes. so it should be same as activatedAt initially

        this.tokens = new ArrayList<>();
    }

    public UUID getCardID() {
        return card_id;
    }

    public UUID getBankAccountID() {
        return bankAccount.getBankAccountID();
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getBalance() {
        return balance;
    }

    public int getAvailableCredit() {
        return availableCredit;
    }

    public void setBalance(int cardAmount) {
        this.balance = cardAmount;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public CardNetwork getCardNetwork() {
        return cardNetwork;
    }

    public CardTier getCardTier() {
        return cardTier;
    }

    public String getLast4() {
        return last4;
    }

    // public String getEncryptedCardNum() {                @Transition to BasicCardInfo
    //     return encryptedCardNum;
    // }

    public BasicCardInfo getBasicCardInfo() {
        return basicCardInfo;
    }

    public Instant getIssuedAtTime() {
        return issuedAt;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public int getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setRiskScore(int riskScore) {
        if (riskScore < 0) {
            this.riskScore = 0;
        } else {
            this.riskScore = riskScore;
        }
    }

    public int getRiskScore() {
        return riskScore;
    }

    public boolean isFlaggedForReview() {
        return flaggedForReview;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public List<CardToken> getTokens() {
        return tokens;
    }
    
}