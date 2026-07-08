package com.sehyeon.monfin.bank.services.bank;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccountInbox;
import com.sehyeon.monfin.bank.model.entity.transactions.Transaction;
import com.sehyeon.monfin.bank.repos.BankAccountInboxRepository;

import jakarta.transaction.Transactional;

@Service
public class BankInboxService {

    @Autowired
    private BankAccountInboxRepository bankInboxRepository;

    public BankInboxService() {}

    /**
     * Realistically, OTP would be sent to a phone number on file
     * However, that would cost money to consume that particular third party API
     * Therefore, this OTP would get sent to the user's bank account "inbox" page
     */
    @Transactional
    public void sendOTP(UUID bankAccountID, String otp) {
        String timeout = "Valid for 10 minutes.";
        BankAccountInbox content = new BankAccountInbox(
            bankAccountID, "One Time Passcode", "Your OTP is: " + otp + "\n" + timeout);
        bankInboxRepository.save(content);
    }

    @Transactional
    public void sendCardStatus(UUID bankAccountID, String cardStatus, Transaction transactionDetails) {
        String defaultMessage = "You've made a purchase on " + transactionDetails.getDate() + " with " + 
            transactionDetails.getMerchantName() + ".";
        String optionalMessage = 
            transactionDetails.getResult().equalsIgnoreCase("declined") ? "\nCurrently your card is " + cardStatus : "";
        String title = transactionDetails.getResult() + ". " +  transactionDetails.getMerchantName() + " on " + transactionDetails.getDate();
        
        BankAccountInbox content = new BankAccountInbox(bankAccountID, title, defaultMessage + optionalMessage);
        bankInboxRepository.save(content);
    }

    // create an enum and change sendOTP to sendMessage
    // the ENUM will be named InboxMessageType, with values OTP, FROZEN, etc
    // Based on the ENUM value, the corresponding message will be sent to the inbox
    // then, sendMessage() will accept bankAccountID and InboxMessageType

    // scratch that, separating by methods seems cleaner
    
}
