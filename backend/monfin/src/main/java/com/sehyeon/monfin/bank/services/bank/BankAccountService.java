package com.sehyeon.monfin.bank.services.bank;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;
import com.sehyeon.monfin.bank.model.card.types.CardType;
import com.sehyeon.monfin.bank.model.entity.UserCredentials;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.services.card.CardIssuanceService;

import jakarta.transaction.Transactional;

/** @@Read banks.docs as to why this class exists the way it does because this will not reflect the real world@@
 * 
 * Manages opening and deleting bank accounts
 * For all functions that exist here, these assumptions apply:
 * 1) Assume user exists in database
 * 2) Assumes UserService.java called getUser function prior to calling this method
 */
@Service
public class BankAccountService {

    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private CardService cardService;
    @Autowired
    private CardIssuanceService cardIssuanceService;

    public BankAccountService() {}

    /**
     * Transactional allows JPA to flush at the end of the function
     * Flush basically means JPA will persist the data to the database
     * save() is often used instead of saveAndFlush() to improve performance
     * Could also completely do all database updates then flush at the very end all pending tasks
     */
    @Transactional
    public void createBankAccount(CreateBankAccountRequest req) {
        BankAccount bankAccount = new BankAccount(req.username(), req.password(), req.fullName(), req.phoneNumber());
        bankRepository.save(bankAccount);
    } // Flushes automatically here because it is tagged transactional

    /**
     * If the user does not have any bank accounts open,
     * a BankAccountNotFoundException will be thrown in runtime (@ the controller layer)
     */
    public Optional<BankAccount> getBankAccountID(UserCredentials userCredentials) {
        return bankRepository.findByUsernameAndPassword(userCredentials.getUsername(), userCredentials.getPassword());
    }

    @Transactional
    public void addCardToAccount(
        BankAccount bankAccount, String fullName, String cardType, String cardNetwork, String cardTier) {
        // Add the following 3 as parameters passed thru by frontend via controllers
        // Right now, they are for testing purposes
        Card card = cardIssuanceService.issueCard(
            bankAccount, fullName, convertStrToCardType(cardType), convertStrToCardNetwork(cardNetwork), convertStrToCardTier(cardTier));
        cardService.createCard(card);
        bankAccount.addCard(card);
    }

    // sms service will call this, and we know the bank account exists in that context
    public String getPhoneNumber(UUID bankAccountID) {
        Optional<BankAccount> bankAccount = bankRepository.findById(bankAccountID);
        return bankAccount.get().getPhoneNumber();
    }

    private CardType convertStrToCardType(String cardType) {
        switch (cardType.toUpperCase()) {
            case "CREDIT":
                return CardType.CREDIT;
            case "DEBIT":
                return CardType.DEBIT;
    
            default:
                return CardType.PREPAID;
        }
    }

    private CardNetwork convertStrToCardNetwork(String cardNetwork) {
        switch (cardNetwork.toUpperCase()) {
            case "VISA":
                return CardNetwork.VISA;
            case "MASTERCARD":
                return CardNetwork.MASTERCARD;
    
            default:
                return CardNetwork.DISCOVER;
        }
    }

    private CardTier convertStrToCardTier(String cardTier) {
        switch (cardTier.toUpperCase()) {
            case "SILVER":
                return CardTier.SILVER;
            case "GOLD":
                return CardTier.GOLD;

            default:
                return CardTier.BRONZE;
        }
    }
    
}
