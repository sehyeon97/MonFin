package com.sehyeon.monfin.bank.security;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.dto.requests.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/bank/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService service) {
        this.authService = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerNewUser(
        @Valid @RequestBody CreateBankAccountRequest request,
        HttpServletResponse response
    ) {
        SignupStatus status = authService.signup(request, response);
        String statusMessage = status.getSignupStatusMessage();
        if (status == SignupStatus.SUCCESS) {
            return ResponseEntity.ok(statusMessage);
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(statusMessage);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        LoginStatus status = authService.login(request, response);
        String statusMessage = status.getLoginStatusMessage();

        if (status == LoginStatus.SUCCESS) {
            return ResponseEntity.ok(statusMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(statusMessage);
    }

    /**
     * Use case: User exits tab that had the web app open. User never exits the browser.
     *          On the same browser, they open the web app again in a new tab.
     *          JWT tokens may be set already. For quality user experience,
     *          validate the jwt when they exist on the frontend.
     *          Then on client side, we show auth form or home page.
     */
    @GetMapping("/still-valid")
    public ResponseEntity<Boolean> isJWTStillValid(Authentication authentication) {
        // if this line is reached, it passed the JwtFilter and Authentication
        return ResponseEntity.ok(true);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginStatus> refresh(
        Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response,
        @CookieValue("refresh_token") String refreshToken
    ) {
        BankAccountDetails bankAccount = (BankAccountDetails) authentication.getPrincipal();
        LoginStatus status =
            authService.refreshSession(bankAccount.getBankAccountID(), refreshToken, request, response);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CookieValue("access_token") String accessToken, HttpServletResponse response, Authentication auth) {
        BankAccountDetails bankAccount = (BankAccountDetails) auth.getPrincipal();
        authService.logout(bankAccount.getBankAccountID());

        // Tell browser to delete cookies by setting maxAge to 0
        // Browsers automatically delete cookies when maxAge is 0
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.noContent().build();
    }
    
}
