package com.sehyeon.monfin.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.ActivateCardRequest;
import com.sehyeon.monfin.bank.dto.requests.DeleteCardRequest;
import com.sehyeon.monfin.bank.dto.requests.NewCardRequest;
import com.sehyeon.monfin.bank.dto.responses.card.AccountCardsResponse;
import com.sehyeon.monfin.bank.dto.responses.card.BasicCardInfoResponse;
import com.sehyeon.monfin.bank.security.BankAccountDetails;
import com.sehyeon.monfin.bank.services.bank.BankAccountService;
import com.sehyeon.monfin.bank.services.bank.CardService;

/**
 * Handles basic bank features such as:
 * 1. Creating or removing card for bank account
 * 2. Review transaction history (Not implemented yet)
 * 3. Change name (first and/or last) (Not implemented yet)
 */
@RestController
@RequestMapping("/api/bank/accounts")
public class BankController {
    @Autowired
    private final BankAccountService bankAccountService;
    @Autowired
    private final CardService cardService;

    public BankController(BankAccountService bankAccountService, CardService cardService) {
        // spring automatically injects the service bean here through constructor injection
        this.bankAccountService = bankAccountService;
        this.cardService = cardService;
    }

    /**
     * create a card for a bank account
     * Later, need to add error cases
     * Like not meeting requirements to create such card
     */
    @PostMapping("/cards/create")
    public ResponseEntity<BasicCardInfoResponse> addCardToBankAccount(@RequestBody NewCardRequest req, Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        String cardType = req.cardType();
        BasicCardInfoResponse result = bankAccountService.addCardToAccount(
            bankAccount.getBankAccountID(),
            bankAccount.getUsername(),
            cardType,
            req.cardNetwork(),
            req.cardTier(),
            cardType == "DEBIT"
        );
        return ResponseEntity.ok(result);
    }

    // Gets all cards owned by bank account regardless of card status
    @GetMapping("/cards/get")
    public ResponseEntity<AccountCardsResponse> getAllOwnedCards(Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        AccountCardsResponse res = 
            bankAccountService.getCardsOwnedByAccount(bankAccount.getBankAccountID());

        System.out.println("LAST FOUR BACKEND: " + res.cards().get(0).lastFour());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/cards/delete")
    public ResponseEntity<?> removeCardFromBankAccount(@RequestBody DeleteCardRequest req, Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        bankAccountService.removeCardFromAccount(bankAccount.getBankAccountID(), req.last4());
        return ResponseEntity.ok("It's always successful.");
    }

    @PostMapping("/cards/activate")
    public ResponseEntity<BasicCardInfoResponse> activateIssuedCard(@RequestBody ActivateCardRequest req, Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        BasicCardInfoResponse res = cardService.activateCard(req, bankAccount.getBankAccountID());
        if (res == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // returns empty body
        }
        return ResponseEntity.ok(res);
    }
    
}
