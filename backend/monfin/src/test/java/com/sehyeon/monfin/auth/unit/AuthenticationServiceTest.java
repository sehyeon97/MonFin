package com.sehyeon.monfin.auth.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sehyeon.monfin.bank.dto.requests.CreateBankAccountRequest;
import com.sehyeon.monfin.bank.dto.requests.LoginRequest;
import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;
import com.sehyeon.monfin.bank.security.AuthenticationService;
import com.sehyeon.monfin.bank.security.BankAccountDetails;
import com.sehyeon.monfin.bank.security.LoginStatus;
import com.sehyeon.monfin.bank.security.SignupStatus;
import com.sehyeon.monfin.bank.services.auth.JwtService;
import com.sehyeon.monfin.bank.services.bank.BankAccountService;

import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authManager;
    
    @Mock
    private JwtService jwtService;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authService;

    /**
     * The field injection variables for access and refresh token time limits
     * use @Value which is a Spring logic, not Mockito logic.
     * Therefore Mockito has no knowledge of @Value variables,
     * so here we define it for Mockito using ReflectionTestUtils
     */
    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(
                authService,
                "accessTokenTimeLimit",
                900
        );

        ReflectionTestUtils.setField(
                authService,
                "refreshTokenTimeLimit",
                86400
        );
    }

    @Test
    public void shouldHaveJWTOnSuccessfulLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("username", "password");

        Authentication authentication = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(authentication);

        BankAccountDetails bankAccount = mock(BankAccountDetails.class);
        when(authentication.getPrincipal()).thenReturn(bankAccount);

        when(jwtService.createJwtAccessRefreshFor(any())).thenReturn("access|refresh");

        // Act
        LoginStatus status = authService.login(request, response);

        // Assert
        assertEquals(LoginStatus.SUCCESS, status);

        verify(response)
            .addCookie(
                argThat(cookie ->
                    cookie.getName().equals("access_token")
                    && cookie.getValue().equals("access")
                )
            );

        verify(response)
            .addCookie(
                argThat(cookie ->
                    cookie.getName().equals("refresh_token")
                    && cookie.getValue().equals("refresh")
                )
            );
    }

    @Test
    public void shouldHaveEmptyCookiesOnFailedLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("invalid_username","wrong_password");
        
        // Act
        LoginStatus status = authService.login(request, response);

        // Assert
        assertEquals(LoginStatus.FAIL, status);

        verify(jwtService, never()).createJwtAccessRefreshFor(any());
        verify(response, never()).addCookie(any());
    }

    @Test
    public void shouldHaveJWTOnSuccessfulSignup() {
        CreateBankAccountRequest request = new CreateBankAccountRequest(
            "username",
            "password",
            "name",
            "null"
        );

        BankAccount bankAccount = new BankAccount(
            "username",
            "encrypted",
            "name",
            "null"
        );
        
        when(passwordEncoder.encode("password")).thenReturn("encrypted");


        when(bankAccountService.createBankAccount(
            new CreateBankAccountRequest("username","encrypted","name","null")))
                .thenReturn(bankAccount);

        when(jwtService.createJwtAccessRefreshFor(bankAccount.getBankAccountID())).thenReturn("access|refresh");

        // Act
        SignupStatus status = authService.signup(request, response);

        // Assert
        assertEquals(SignupStatus.SUCCESS, status);

        verify(response)
            .addCookie(
                argThat(cookie ->
                    cookie.getName().equals("access_token")
                    && cookie.getValue().equals("access")
                )
            );

        verify(response)
            .addCookie(
                argThat(cookie ->
                    cookie.getName().equals("refresh_token")
                    && cookie.getValue().equals("refresh")
                )
            );
    }

    @Test
    public void shouldHaveEmptyCookiesOnFailedSignup() {}
    
}
