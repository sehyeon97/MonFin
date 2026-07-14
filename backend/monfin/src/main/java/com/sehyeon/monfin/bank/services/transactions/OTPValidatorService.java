package com.sehyeon.monfin.bank.services.transactions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.ProcessorOTPCallbackRequest;
import com.sehyeon.monfin.bank.dto.responses.CardAuthorizationResponse;
import com.sehyeon.monfin.bank.dto.responses.TransactionData;
import com.sehyeon.monfin.bank.dto.responses.TransactionResponse;
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

    // messenger for payment processor backend
    @Autowired
    private PPCallbackService callbackService;

    private static final SecureRandom random = new SecureRandom();

    public OTPValidatorService() {}

    // TODO: In the future, implement a feature so that not every verified transaction is approved
    // for example, what if the customer doesn't have enough funds? it should be declined, not approved
    public List<TransactionResponse> validateOTP(UUID otpID, String userOTP, TransactionData data) {
        Optional<OneTimePasscode> otpData = otpRepository.findById(otpID);
        if (otpData.isEmpty() || !otpData.get().getOTP().equals(userOTP)) {
            return new ArrayList<TransactionResponse>();
        }

        // send the validation results to the payment processor's backend (server-to-server communication)
        OneTimePasscode otp = otpData.get();
        List<UUID> transactionIDs = transactionOTPRepository.findAllByOtpID(otpID).stream().map(TransactionOTP::getTransactionId).toList();
        ProcessorOTPCallbackRequest req = 
            new ProcessorOTPCallbackRequest(transactionIDs, "APPROVED", "authorized");
        callbackService.notifyPaymentProcessor(otp.getPPCallbackUrl(), req);

        // Create response
        List<TransactionResponse> response = new ArrayList<>();
        for (int i = 0; i < transactionIDs.size(); i++) {
            CardAuthorizationResponse caRes = new CardAuthorizationResponse(
                true, "authorized", "", "");
            TransactionResponse tRes = new TransactionResponse(data, caRes);
            response.add(tRes);
        }

        // remove this transaction from otp repository
        otpRepository.delete(otp);
        otpRepository.flush(); // makes the deletion immediate
        return response;
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
