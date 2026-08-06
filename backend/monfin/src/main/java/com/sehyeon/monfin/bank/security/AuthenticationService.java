package com.sehyeon.monfin.bank.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.dto.requests.LoginRequest;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.services.auth.JwtService;
import com.sehyeon.monfin.bank.services.bank.BankAccountService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final BankAccountService bankAccountService;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.jwt.expiration.access}")
    private int accessTokenTimeLimit;
    @Value("${auth.jwt.expiration.refresh}")
    private int refreshTokenTimeLimit;

    public AuthenticationService(
        AuthenticationManager manager, JwtService jwtService,
        BankAccountService bankAccountService, PasswordEncoder passwordEncoder) {
        this.authManager = manager;
        this.jwtService = jwtService;
        this.bankAccountService = bankAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     1. login request
     2A. authenticate using AuthenticationManager.authenticate()
     2B. authenticate internally does password comparison by PasswordEncoder.matches(raw, hashed)
     */
    public LoginStatus login(LoginRequest request, HttpServletResponse response) {
        Authentication auth;
        try {
            auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (Exception e) {
            return LoginStatus.FAIL;
        }

        if (auth == null) {
            return LoginStatus.FAIL;
        }

        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccount.getBankAccountID());
        String[] accessAndRefreshJwt = jwt.split("\\|");

        Cookie accessCookie = new Cookie("access_token", accessAndRefreshJwt[0]);
        Cookie refreshCookie = new Cookie("refresh_token", accessAndRefreshJwt[1]);

        // Accepts seconds: Minutes * how many seconds are in a minute
        accessCookie.setMaxAge(accessTokenTimeLimit);
        refreshCookie.setMaxAge(refreshTokenTimeLimit);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return LoginStatus.SUCCESS;
    }

    /**
      1. Phone number must be unique (check if already in use)
      2. Hash password and create new bank account
      3. Generate JWT
      4. Set HttpOnly cookie
      5. Return signup status (success | fail) Created
     */
    public SignupStatus signup(CreateBankAccountRequest request, HttpServletResponse response) {
        // if phone number wasn't in use, .createBankAccount() hashed password

        // Step 1: check uniqueness of phone number and username
        if (!isUniquePhoneNumberAndUsername(request)) {
            return SignupStatus.FAIL;
        }

        // Step 2: save the new bank account to database with hashed password
        CreateBankAccountRequest requestWithHashedPassword =
            new CreateBankAccountRequest(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.fullName(),
                request.phoneNumber()
            );
        BankAccount bankAccount = bankAccountService.createBankAccount(requestWithHashedPassword);

        // Step 3: Generate JWT (Access_token | Refresh_token)
        String jwt = jwtService.createJwtAccessRefreshFor(bankAccount.getBankAccountID());
        String[] accessAndRefreshJwt = jwt.split("\\|");

        // Step 4: Set HttpOnly Cookie (Automatically sent to frontend as header)
        Cookie accessCookie = new Cookie("access_token", accessAndRefreshJwt[0]);
        Cookie refreshCookie = new Cookie("refresh_token", accessAndRefreshJwt[1]);

        // Accepts seconds: Minutes * how many seconds are in a minute
        accessCookie.setMaxAge(accessTokenTimeLimit);
        refreshCookie.setMaxAge(refreshTokenTimeLimit);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return SignupStatus.SUCCESS;
    }

    private boolean isUniquePhoneNumberAndUsername(CreateBankAccountRequest req) {
        return !bankAccountService.isPhoneNumberInUse(req.phoneNumber())
            && !bankAccountService.isUsernameInUse(req.username());
    }
    
}
