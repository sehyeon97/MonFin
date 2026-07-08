package com.sehyeon.monfin.bank.services.transactions;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.ProcessorOTPCallbackRequest;
import com.sehyeon.monfin.bank.dto.responses.VerifyOTPResponse;
import com.sehyeon.monfin.bank.model.entity.transactions.OneTimePasscode;
import com.sehyeon.monfin.bank.model.entity.transactions.TransactionOTP;
import com.sehyeon.monfin.bank.repos.transactions.OTPRepository;
import com.sehyeon.monfin.bank.repos.transactions.TransactionOTPRepository;

import jakarta.transaction.Transactional;

@Service
public class OTPValidatorService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private TransactionOTPRepository transactionOTPRepository;

    // @Autowired
    // private TransactionRepository transactionRepository;

    // messenger for payment processor backend
    @Autowired
    private PPCallbackService callbackService;

    private static final SecureRandom random = new SecureRandom();

    public OTPValidatorService() {}

    public VerifyOTPResponse validateOTP(UUID otpID, String userOTP) {
        Optional<OneTimePasscode> otpData = otpRepository.findById(otpID);
        if (otpData.isEmpty() || !otpData.get().getOTP().equals(userOTP)) {
            return new VerifyOTPResponse(false, "");
        }

        // Optional<Transaction> transactionData = transactionRepository.findById(transactionID);
        // if (transactionData.isEmpty()) {
        //     return new VerifyOTPResponse(false, "");
        // }

        // // set the transaction to approved in transaction repository
        // transactionData.get().setResultToPending();
        // transactionRepository.flush();

        // send the validation results to the payment processor's backend (server-to-server communication)
        OneTimePasscode otp = otpData.get();
        List<UUID> transactionIDs = transactionOTPRepository.findAllByOtpID(otpID).stream().map(TransactionOTP::getTransactionId).toList();
        ProcessorOTPCallbackRequest req = 
            new ProcessorOTPCallbackRequest(transactionIDs, "APPROVED", "authorized");
        callbackService.notifyPaymentProcessor(otp.getPPCallbackUrl(), req);

        // remove this transaction from otp repository
        otpRepository.delete(otp);
        otpRepository.flush(); // makes the deletion immediate
        return new VerifyOTPResponse(true, "authorized");
    }

    @Transactional
    public UUID storeOTP(UUID transactionID, String callbackUrl) {
        String otpStr = generateOTP();
        OneTimePasscode otp = new OneTimePasscode(otpStr, callbackUrl);
        otpRepository.save(otp);
        transactionOTPRepository.save(new TransactionOTP(otp.getOtpID(), transactionID));
        return otp.getOtpID();
    }

    @Transactional
    public void addTransactionToOTP(UUID otpID, UUID transactionID) {
        transactionOTPRepository.save(new TransactionOTP(otpID, transactionID));
    }

    public String getOTPStringByID(UUID otpID) {
        return otpRepository.findById(otpID).get().getOTP();
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
