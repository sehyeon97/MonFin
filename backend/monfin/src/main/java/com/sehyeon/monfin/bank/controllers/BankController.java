package com.sehyeon.monfin.bank.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.dto.requests.NewCardRequest;
import com.sehyeon.monfin.bank.dto.responses.NewCardResponse;
import com.sehyeon.monfin.bank.model.entity.UserCredentials;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.services.bank.BankAccountService;

import jakarta.validation.Valid;

/**
 * Handles basic bank features such as:
 * 1. validate user by credentials and respond with bank account id for that session
 * 2. add or remove card in bank account
 * 3. Change name (first and/or last)
 */
@RestController
@RequestMapping("/api/bank/accounts")
public class BankController {

    // Holds multiple user accounts by their bank account ID
    private final Map<UserCredentials, BankAccount> bankAccounts;
    @Autowired
    private final BankAccountService bankAccountService;

    @Autowired
    private BankRepository bankRepository;

    public BankController(BankAccountService bankAccountService) {
        this.bankAccounts = new HashMap<>();
        // spring automatically injects the service bean here through constructor injection
        this.bankAccountService = bankAccountService;
        // for testing, input a bank account id into database and add it to this map
    }

    // gets bank account ID for frontend 
    // frontend will pass in username and password as a request
    @PostMapping("/login")
    public ResponseEntity<?> getBankAccount(@RequestBody UserCredentials userCredentials) {
        // if bankAccountID was cached, we don't need to interact with database
        if (bankAccounts.containsKey(userCredentials)) {
            return ResponseEntity.ok(bankAccounts.get(userCredentials));
        }

        Optional<BankAccount> bankAccount = bankAccountService.getBankAccountID(userCredentials);
        
        // Return bank account id UUID if successful lookup and cache user credentials
        if (bankAccount.isPresent()) {
            BankAccount bankAccountData = bankAccount.get();
            bankAccounts.put(userCredentials, bankAccountData);
            return ResponseEntity.ok(bankAccountData.getBankAccountID());
        }
        // Return a String message if no bank account with associated username and password is found
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    /**
     * create a card for a bank account
     * For MVP, bankAccountID will be passed between client and server
     * Later, will change to JWT (JSON Web Token)
     */
    @PostMapping("/cards/create")
    public ResponseEntity<?> addCardToBankAccount(@RequestBody NewCardRequest req) {
        Optional<BankAccount> account = bankRepository.findById(req.bankAccountID());

        // bank account is not found
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NewCardResponse("Invalid bank account"));
        }

        bankAccountService.addCardToAccount(
            account.get(), account.get().getFullName(), req.cardType(), req.cardNetwork(), req.cardTier());

        return ResponseEntity.ok(new NewCardResponse("Successfully added card to account"));
    }

    // Create bank account
    @PostMapping("/create")
    public ResponseEntity<String> openBankAccount(@Valid @RequestBody CreateBankAccountRequest req) {
        bankAccountService.createBankAccount(req);
        return ResponseEntity.ok("Successfully opened a new bank account");
    }
    
}
