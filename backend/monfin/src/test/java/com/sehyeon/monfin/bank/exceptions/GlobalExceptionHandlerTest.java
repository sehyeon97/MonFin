package com.sehyeon.monfin.bank.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GlobalExceptionHandlerTest {
    
    @Test
    void userNotFoundExceptionTest() {}

}
