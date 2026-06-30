package com.sehyeon.monfin.bank.model.entity.bank;
// package com.sehyeon.monfin.model.entity.bank;

// import java.util.UUID;


// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "card_details")
// public class CardDetails {

//     @Id
//     @GeneratedValue
//     private UUID id; // primary key (this id is unique to each row)
//     private final UUID bankAccountID; // foreign key (called in relation to bank_accounts; this id can be repetitive)
//     private final UUID cardID;

//     private String encryptedCardNum;
//     private int cardAmount; // in cents

//     public CardDetails(UUID bankAccountID, UUID cardID, String encryptedCardNum, int cardAmount) {
//         this.bankAccountID = bankAccountID;
//         this.cardID = cardID;
//         this.encryptedCardNum = encryptedCardNum;
//         this.cardAmount = cardAmount;
//     }

//     public UUID getBankAccountID() {
//         return bankAccountID;
//     }

//     public UUID getCardID() {
//         return cardID;
//     }

//     public int getCardAmount() {
//         return cardAmount;
//     }

//     public void setCardAmount(int cardAmount) {
//         this.cardAmount = cardAmount;
//     }

//     public String getEncryptedCardNum() {
//         return encryptedCardNum;
//     }
    
// }
