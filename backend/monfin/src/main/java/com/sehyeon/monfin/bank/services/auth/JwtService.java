package com.sehyeon.monfin.bank.services.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;

    // JWT Access Token | Bank Account ID
    private Map<String, UUID> accessTokenOwner;

    // access token lifecycle (15min)
    private static final Duration ACCESS_TOKEN_LIFECYCLE = Duration.ofMinutes(15);
    // refresh token lifecycle (1 week)
    private static final Duration REFRESH_TOKEN_LIFECYCLE = Duration.ofDays(1);

    public JwtService(@Value("${auth.jwt.key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        accessTokenOwner = new HashMap<>();
    }

    /**
     * Generates both access and refresh tokens separated by |
     * Should be used when users signup or login
     */
    public String createJwtAccessRefreshFor(UUID bankAccountID) {
        String random = UUID.randomUUID().toString().replaceAll("-", "");

        Date accessLifecycle = Date.from(Instant.now().plus(ACCESS_TOKEN_LIFECYCLE));
        Date refreshLifecycle = Date.from(Instant.now().plus(REFRESH_TOKEN_LIFECYCLE));

        String accessToken = buildJwt(random, bankAccountID, accessLifecycle);
        String refreshToken = buildJwt(random, bankAccountID, refreshLifecycle);

        accessTokenOwner.put(accessToken, bankAccountID);
        return accessToken + "|" + refreshToken;
    }

    // Should be called for all requests other than login and signup
    public String refreshAccessToken() {
        return "Not implemented yet";
    }

    // User signed out
    public void removeJwtFor(String jwt) {
        accessTokenOwner.remove(jwt);
    }

    public boolean isValidJWT(String accessToken, UUID bankAccountID) {
        // System.out.println("ACCESS TOKEN MAP SIZE: " + accessTokenOwner.size());
        if (!accessTokenOwner.containsKey(accessToken)) {
            return false;
        }
        // System.out.println(accessTokenOwner.get(accessToken) == bankAccountID);
        // System.out.println("Stored Bank account id: " + accessTokenOwner.get(accessToken));
        // System.out.println("Given bank account id: " + bankAccountID.toString());
        // Fix UUID comparison from == to .equals()
        return accessTokenOwner.get(accessToken).equals(bankAccountID);
    }

    public Claims parseJwt(String jwt) {
        try {
            return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildJwt(String subject, UUID userID, Date expiration) {
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .claim("id", userID)
            // 15min past issued time
            .expiration(expiration)
            .signWith(this.secretKey)
            .compact();
    }
    
}
