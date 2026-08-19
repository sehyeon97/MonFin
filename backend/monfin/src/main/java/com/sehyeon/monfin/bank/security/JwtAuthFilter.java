package com.sehyeon.monfin.bank.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sehyeon.monfin.bank.services.auth.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** This is for every request other than signup and login 
 *  && only cares about normal requests (access token verifications)
 * JwtAuthFilter runs after Spring Security (my SS allows signup and login to bypass)
 1. Extract JWT from cookie
 2. Validate JWT signature + expiration
 3. Extract user information (bank account id)
 4. Load user details (BankAccountDetails)
 5. Create Authentication object
 6. Store it in SecurityContext
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter { // means it runs once per http request

    private final JwtService jwtService;
    private final BankAccountDetailsService bankAccountDetailsService;

    private static final String JWT_COOKIE_NAME = "access_token";

    public JwtAuthFilter(JwtService jwtService, BankAccountDetailsService bankAccountDetailsService) {
        this.jwtService = jwtService;
        this.bankAccountDetailsService = bankAccountDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                System.out.println("===== JWT FILTER =====");
        String accessToken = extractAccessToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID bankAccountID = getBankAccountID(accessToken);

        if (bankAccountID != null && 
            SecurityContextHolder.getContext().getAuthentication() == null) {
            BankAccountDetails bankAccountDetails = bankAccountDetailsService.loadBankAccountByID(bankAccountID);

            // make sure this bank account has ownership to correct access token
            if (jwtService.isValidJWT(accessToken, bankAccountID)) {
                // at this point, the bank account is authenticated
                // and this is their identity and permissions
                // because we don't have roles, we use the two parameter object (principal + credentials)
                // credentials is password and we don't need the password after authentication, therefore set to null
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    bankAccountDetails, null, bankAccountDetails.getAuthorities()
                );

                // For the rest of the request, this user is authenticated
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("authentication: " + SecurityContextHolder.getContext().getAuthentication());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Refresh token is handled by a separate endpoint to refresh access token
     * CSRF token is handled by Spring Security
     */
    private String extractAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(JWT_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private UUID getBankAccountID(String accessToken) {
        Claims claims = jwtService.parseJwt(accessToken);
        return UUID.fromString(claims.get("id", String.class));
    }
    
}
