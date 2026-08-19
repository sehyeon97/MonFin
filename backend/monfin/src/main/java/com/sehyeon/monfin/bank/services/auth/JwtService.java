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

    // Bank Account ID | JWT Access Token
    private Map<UUID, String> accessTokenOwner;

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

        accessTokenOwner.put(bankAccountID, accessToken);
        return accessToken + "|" + refreshToken;
    }

    // Should be called for all requests to refresh session other than login and signup
    public String refreshAccessToken(UUID bankAccountID) {
        String random = UUID.randomUUID().toString().replaceAll("-", "");

        Date accessLifecycle = Date.from(Instant.now().plus(ACCESS_TOKEN_LIFECYCLE));
        String accessToken = buildJwt(random, bankAccountID, accessLifecycle);

        accessTokenOwner.put(bankAccountID, accessToken);
        return accessToken;
    }

    // User signed out
    public void removeJwtFor(UUID bankAccountID) {
        accessTokenOwner.remove(bankAccountID);
    }

    public boolean isValidJWT(String accessToken, UUID bankAccountID) {
        // System.out.println("ACCESS TOKEN MAP SIZE: " + accessTokenOwner.size());
        if (!accessTokenOwner.containsKey(bankAccountID)) {
            return false;
        }
        // System.out.println(accessTokenOwner.get(accessToken) == bankAccountID);
        // System.out.println("Stored Bank account id: " + accessTokenOwner.get(accessToken));
        // System.out.println("Given bank account id: " + bankAccountID.toString());
        // Fix UUID comparison from == to .equals()
        return accessTokenOwner.get(bankAccountID).equals(accessToken);
    }

    public boolean isRefreshTokenValid(String refreshToken, UUID bankAccountID) {
        Claims claims = parseJwt(refreshToken);

        // refresh token signed with wrong secret key
        if (claims == null) {
            return false;
        }

        // refresh token expired
        if (claims.getExpiration().before(new Date())) {
            return false;
        }

        UUID otherBankAccountID = 
            UUID.fromString(claims.get("id", String.class));

        // suspiscious wrong id passed in refresh token or hacked account?
        if (!bankAccountID.equals(otherBankAccountID)) {
            return false;
        }

        return true;
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
            .claim("id", userID.toString()) // JSON doesn't have native UUID type
            // 15min past issued time
            .expiration(expiration)
            .signWith(this.secretKey)
            .compact();
    }
    
}
