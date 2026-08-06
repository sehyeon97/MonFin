package com.sehyeon.monfin.bank.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.NewCardRequest;
import com.sehyeon.monfin.bank.dto.responses.NewCardResponse;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.repos.BankRepository;
import com.sehyeon.monfin.bank.services.bank.BankAccountService;

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
    private BankRepository bankRepository;

    public BankController(BankAccountService bankAccountService) {
        // spring automatically injects the service bean here through constructor injection
        this.bankAccountService = bankAccountService;
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
    
}
