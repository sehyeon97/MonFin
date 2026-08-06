package com.sehyeon.monfin.auth.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sehyeon.monfin.bank.services.auth.JwtService;

import io.jsonwebtoken.Claims;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService("My-super-secret-test-key-even-I-am-confused-about");
    }

    /**
     * Given a bank account id,
     * return both tokens separated by | as one String
     */
    @Test
    public void shouldCreateValidAccessRefreshTokens() {
        // Arrange
        UUID bankAccountID = UUID.randomUUID();

        // Act
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccountID);
        String[] accessAndRefresh = jwt.split("\\|");

        // Assert
        assertEquals(2, accessAndRefresh.length);
    }

    @Test
    public void shouldReturnTrueForCachedJWTOwnership() {
        // Arrange
        UUID bankAccountID = UUID.randomUUID();

        // Act
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccountID);
        String[] accessAndRefresh = jwt.split("\\|");
        String accessToken = accessAndRefresh[0];
        boolean isValid = jwtService.isValidJWT(accessToken, bankAccountID);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void shouldReturnFalseWhenJWTOwnershipNotEstablished() {
        // Arrange
        UUID bankAccountID = UUID.randomUUID();

        // Act
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccountID);
        String[] accessAndRefresh = jwt.split("\\|");
        String accessToken = accessAndRefresh[0];
        boolean isValid = jwtService.isValidJWT(accessToken, UUID.randomUUID());

        // Assert
        assertFalse(isValid);
    }

    /**
     * access token has 15min lifecycle
     * refresh token has 1 day lifecycle
     */
    @Test
    public void shouldParseJWTAndExtractClaimAndExpiration() {
        // Arrange
        UUID bankAccountID = UUID.randomUUID();
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccountID);
        String[] accessAndRefresh = jwt.split("\\|");
        String accessToken = accessAndRefresh[0];

        // Act
        Claims claims = jwtService.parseJwt(accessToken);
        
        // Assert
        // Ensures type-safety because JJWT does not support UUID
        assertEquals(bankAccountID.toString(), claims.get("id", String.class));
    }
    
}
