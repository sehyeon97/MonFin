package com.sehyeon.monfin.bank.repos.bank;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

// TODO: need to finish CardTokenService.java first before writing this as no card is saved under a bank account yet
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardAuthorizationTest {
    
}
