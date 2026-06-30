// package com.sehyeon.monfin.bank.model.entity.bank;

// import java.util.UUID;

// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "merchants")
// public class Merchant {

//     @Id
//     @GeneratedValue
//     private UUID merchantID;

//     // One merchant owns one bank account
//     // A bank account can only be owned by one merchant
//     @OneToOne(mappedBy = "bankAccountID")
//     private BankAccount bankAccount;

//     protected Merchant() {}
    
// }
