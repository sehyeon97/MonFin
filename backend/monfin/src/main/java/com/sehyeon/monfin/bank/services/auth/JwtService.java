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

    private @Value("${auth.jwt.key}") static String secretKey;
    private static final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    // JWT Access Token | Bank Account ID
    private Map<String, UUID> accessTokenOwner;

    // access token lifecycle (15min)
    private static final Duration ACCESS_TOKEN_LIFECYCLE = Duration.ofMinutes(15);
    // refresh token lifecycle (1 week)
    private static final Duration REFRESH_TOKEN_LIFECYCLE = Duration.ofDays(7);

    public JwtService() {
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
        if (!accessTokenOwner.containsKey(accessToken)) {
            return false;
        }
        return accessTokenOwner.get(accessToken) == bankAccountID;
    }

    public Claims parseJwt(String jwt) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
    }

    private String buildJwt(String subject, UUID userID, Date expiration) {
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .claim("id", userID)
            // 15min past issued time
            .expiration(expiration)
            .signWith(key)
            .compact();
    }
    
}
