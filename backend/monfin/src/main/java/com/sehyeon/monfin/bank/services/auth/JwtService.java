package com.sehyeon.monfin.bank.services.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private @Value("${auth.jwt.key}") static String secretKey;
    private static final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    private Set<String> dispatchedJwts;

    public JwtService() {
        dispatchedJwts = new HashSet<>();
    }

    public String createFirstJwtFor(String username) {
        dispatchedJwts.add(username);
        return buildJwt(username);
    }

    public String refreshJwt(String jwt) {
        if (!dispatchedJwts.contains(jwt)) {
            return "";
        }

        return generateNewJwtBasedOnCurrent(jwt);
    }

    // User signed out
    public void removeJwtFor(String username) {
        dispatchedJwts.remove(username);
    }

    private String generateNewJwtBasedOnCurrent(String jwt) {
        StringBuilder subject = new StringBuilder(10);
        Random random = new Random();
        int upperBound = jwt.length();
        for (int i = 0; i < subject.capacity(); i++) {
            subject.append(jwt.charAt(random.nextInt(upperBound)));
        }

        return buildJwt(subject.toString());
    }

    private String buildJwt(String subject) {
        return Jwts.builder()
            .subject(subject.toString())
            .issuedAt(new Date())
            // 15min past issued time
            .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(15).toMillis()))
            .signWith(key)
            .compact();
    }
    
}
