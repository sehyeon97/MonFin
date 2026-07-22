package com.sehyeon.monfin.bank.services.payment;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sehyeon.monfin.bank.dto.requests.CardTokenizationRequest;
import com.sehyeon.monfin.bank.model.entity.bank.Card;
import com.sehyeon.monfin.bank.model.entity.tsp.CardToken;
import com.sehyeon.monfin.bank.model.payment.TokenizedCardInfo;
import com.sehyeon.monfin.bank.repos.CardTokenRepository;

import jakarta.transaction.Transactional;

/**
 * When the Payment Processor sends card credentials to token service provider,
 * this is the service called to generate the token for the card
 */
@Service
public class PPCardTokenizer {

    @Autowired
    private CardTokenRepository cardTokenRepository;

    // constants used to generate the random token
    private static final String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
    private static final SecureRandom RANDOMIZER = new SecureRandom();
    private static final int RANDOM_LENGTH = 6;
    private static final String ALGORITHM = "HmacSHA512"; // Hash-based Message Authentication Code
    
    public PPCardTokenizer() {}

    /**
     * First 4 characters will always say tok_ to identify it as a token
     * Next 6 characters will be generated in random
     * The remaining characters will be the actual generated token based on the request
     */
    @Transactional
    public TokenizedCardInfo generateCardToken(CardTokenizationRequest request, Card card) {
        String first4 = "tok_";
        String middle6 = generateRandomChars();
        String lastX = generateCryptogram(request, middle6);

        String completeToken = first4 + middle6 + lastX;
        CardToken cardToken = new CardToken(completeToken, card);
        
        cardTokenRepository.save(cardToken);

        return new TokenizedCardInfo(
            completeToken, request.pan().substring(12, 16), request.expMonth(), request.expYear());
    }

    private String generateRandomChars() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = RANDOMIZER.nextInt(AVAILABLE_CHARACTERS.length());
            sb.append(AVAILABLE_CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    /**
     * The next6 ensures the cryptogram is NOT deterministic
     * HMAC by itself would have made it deterministic because the request will be the same every time for that card
     * Here, we want the key to be UNIQUE while tokenizing the request using the same hashing algorithm
     * 
     * This token is used by the payment processor and/or merchant to charge the customer's card
     * The Bank / TSP looks up the token and identifies the card to charge
     * IF in the future, the PP or merchant makes the same request to ask for a token on the same card,
     * it will automatically be rejected (although this part isn't part of the MVP hence not implemented yet)
     * Simply, it rejects in suspicion of fraud
     */
    private String generateCryptogram(CardTokenizationRequest request, String next6) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec spec = new SecretKeySpec(next6.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(spec);

            String requestParams = 
                request.userID() + "|" + request.pan() + "|" + request.cvv() + "|" + request.expMonth() + "|" + request.expYear();
            byte[] hmac = mac.doFinal(requestParams.getBytes(StandardCharsets.UTF_8));

            return bytestoHexString(hmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String bytestoHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

}
