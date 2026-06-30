package com.sehyeon.monfin.bank.services.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CardTokenMakerTest {

    @Autowired
    private CardTokenMaker cardTokenMaker;

    @Test
    public void encryptionShouldNotBeNull() {
        String encrypted = cardTokenMaker.encrypt("123456");

        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }

    @Test
    public void shouldEncryptMiddleSixDigits() {
        String encrypted = cardTokenMaker.encrypt("123456");

        assertNotEquals("123456", encrypted);
    }

    @Test
    public void doesPreserveFormat() {
        String encrypted = cardTokenMaker.encrypt("123456");

        assertEquals(6, encrypted.length());
        assertTrue(encrypted.matches("\\d{6}"));
    }

    @Test
    public void isDeterministic() {
        String middleSixDigits = "123456";
        String encrypted = cardTokenMaker.encrypt(middleSixDigits);
        String otherEncrypted = cardTokenMaker.encrypt(middleSixDigits);

        assertEquals(encrypted, otherEncrypted);
    }
    
}
