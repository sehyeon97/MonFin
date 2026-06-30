package com.sehyeon.monfin.bank.services.transactions;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.ProcessorOTPCallbackRequest;
import com.sehyeon.monfin.bank.dto.responses.VerifyOTPResponse;
import com.sehyeon.monfin.bank.model.entity.bank.OneTimePasscode;
import com.sehyeon.monfin.bank.model.entity.bank.Transaction;
import com.sehyeon.monfin.bank.repos.OTPRepository;
import com.sehyeon.monfin.bank.repos.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class OTPValidatorService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // messenger for payment processor backend
    @Autowired
    private PPCallbackService callbackService;

    private static final SecureRandom random = new SecureRandom();

    public OTPValidatorService() {}

    public VerifyOTPResponse validateOTP(UUID transactionID, String userOTP) {
        Optional<OneTimePasscode> otpData = otpRepository.findByTransactionID(transactionID);
        if (otpData.isEmpty() || !otpData.get().getOTP().equals(userOTP)) {
            return new VerifyOTPResponse(false, "");
        }

        Optional<Transaction> transactionData = transactionRepository.findById(transactionID);
        if (transactionData.isEmpty()) {
            return new VerifyOTPResponse(false, "");
        }

        // set the transaction to approved in transaction repository
        transactionData.get().setResultToPending();
        transactionRepository.flush();

        // send the validation results to the payment processor's backend (server-to-server communication)
        OneTimePasscode otp = otpData.get();
        ProcessorOTPCallbackRequest req = 
            new ProcessorOTPCallbackRequest(transactionID, "APPROVED", "authorized");
        callbackService.notifyPaymentProcessor(otp.getPPCallbackUrl(), req);

        // remove this transaction from otp repository
        otpRepository.delete(otp);
        otpRepository.flush(); // makes the deletion immediate
        return new VerifyOTPResponse(true, "authorized");
    }

    @Transactional
    public String storeOTP(UUID transactionID, String callbackUrl) {
        String otp = generateOTP();
        otpRepository.save(new OneTimePasscode(transactionID, otp, callbackUrl));
        return otp;
    }

    private String generateOTP() {
        int otp = 0;
        for (int i = 0; i < 6; i++) {
            otp *= 10;
            otp += random.nextInt(10);
        }
        return Integer.toString(otp);
    }
    
}
