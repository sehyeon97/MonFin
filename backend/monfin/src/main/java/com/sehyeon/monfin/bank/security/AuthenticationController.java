package com.sehyeon.monfin.bank.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.dto.requests.LoginRequest;

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
    
}
