package com.sehyeon.monfin.transaction.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;

import com.sehyeon.monfin.bank.dto.responses.VerifyOTPResponse;
import com.sehyeon.monfin.bank.model.entity.transactions.OneTimePasscode;
import com.sehyeon.monfin.bank.repos.transactions.OTPRepository;
import com.sehyeon.monfin.bank.repos.transactions.TransactionOTPRepository;
import com.sehyeon.monfin.bank.services.transactions.OTPValidatorService;
import com.sehyeon.monfin.bank.services.transactions.PPCallbackService;

@ExtendWith(MockitoExtension.class)
public class OTPValidatorServiceTest {
    
    @Mock
    private OTPRepository otpRepository;

    // @Mock
    // private TransactionRepository transactionRepository;

    @Mock
    private PPCallbackService callbackService;

    @Mock
    private TransactionOTPRepository transactionOTPRepository;

    @InjectMocks
    private OTPValidatorService otpService;

    private static final String CORRECT_OTP = "123456";
    private static final String WRONG_OTP = "000000";
    private static final String PAYMENT_PROCESSOR_CALLBACK_URL = "www.hello-world.com";

    @Test
    @Rollback(true)
    public void shouldDeclineInvalidOTP() {
        // Arrange
        UUID transactionID = UUID.randomUUID();
        OneTimePasscode otp = new OneTimePasscode(CORRECT_OTP, PAYMENT_PROCESSOR_CALLBACK_URL);

        // Act
        otpRepository.saveAndFlush(otp);
        VerifyOTPResponse res = otpService.validateOTP(transactionID, WRONG_OTP);

        // Assert
        assertFalse(res.verified());
        assertEquals("", res.authorizationCode());
    }

    @Test
    @Rollback(true)
    public void shouldDeclineInvalidTransaction() {
        // Arrange
        OneTimePasscode otp = new OneTimePasscode(CORRECT_OTP, PAYMENT_PROCESSOR_CALLBACK_URL);

        // Act
        otpRepository.saveAndFlush(otp);
        VerifyOTPResponse res = otpService.validateOTP(UUID.randomUUID(), CORRECT_OTP);

        // Assert
        assertFalse(res.verified());
        assertEquals("", res.authorizationCode());
    }

    /**
     * Transaction is set to "Pending"
     * Verify Payment Processor is notified with (what it gets notified with is tested in callbackservicetest)
     * Verify OTP repository was called once to delete this current OTP
     * Verify the correct VerifyOTPResponse was returned
     * This also requires integration testing
     */
    @Test
    public void shouldValidateOTPAndMakeAdjustments() {
        // Arrange
        UUID transactionID = UUID.randomUUID();
        // Transaction transaction = new Transaction(
        //     transactionID, UUID.randomUUID(), UUID.randomUUID(),
        //     UUID.randomUUID().toString(), "30", "6", "2026",
        //     "Amazon", UUID.randomUUID(), 100, Instant.now(), "");
        OneTimePasscode otp = new OneTimePasscode(CORRECT_OTP, PAYMENT_PROCESSOR_CALLBACK_URL);

        // Act
        when(otpRepository.findById(any())).thenReturn(Optional.of(otp));
        VerifyOTPResponse res = otpService.validateOTP(transactionID, CORRECT_OTP);

        // Check that result went from an empty string to Pending
        // assertTrue(transactionRepository.findById(transactionID).get().getResult().equalsIgnoreCase("Pending"));
        // Verify that the payment processor is notified
        verify(callbackService).notifyPaymentProcessor(any(), any());
        // Otp repository is called once to delete current OTP from table
        verify(otpRepository, times(1)).delete(otp);
        // Ensure VerifyOTPResponse has correct arguments
        assertTrue(res.verified());
        assertEquals("authorized", res.authorizationCode());
    }

    // THIS REQUIRES INTEGRATION TESTING AND CANNOT BE DONE WITH MOCKS
    // @Test
    // @Rollback // true by default
    // public void shouldReturnRandomUUID() {
    //     // Arrange
    //     UUID transactionID = UUID.randomUUID();

    //     // Act
    //     UUID otpID = otpService.storeOTP(transactionID, PAYMENT_PROCESSOR_CALLBACK_URL);

    //     // Assert
    //     assertNotNull(otpID);
    // }

}
