package com.sehyeon.monfin.bank.services.card;

import java.security.GeneralSecurityException;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.bouncycastle.jcajce.spec.FPEParameterSpec;

/**
 * Work in progress . . .
 */
@Component
public class CardTokenMaker {

    // tells java how to transform cipher
    private static final String TRANSFORMATION = "AES/FF3-1/NoPadding";
    // tells java the cipher's provider - Bouncy Castle
    private static final String PROVIDER = "BC";

    private SecretKey key; // AES
    private byte[] tweak;
    private int radix;

    // Because the parameters are annotated with @Value,
    // Spring will call this constructor for us whenever the service is used.
    public CardTokenMaker(@Value("${crypto.fpe.key}") String aesKey,
        @Value("${crypto.fpe.tweak}") String hexTweak,
        @Value("${crypto.fpe.radix}") int radix) {

        byte[] keyBytes = HexFormat.of().parseHex(aesKey);
        this.key = new SecretKeySpec(keyBytes, "AES"); // tells Java it's an AES key
        this.tweak = HexFormat.of().parseHex(hexTweak); // 7 bytes = 14 hex chars
        this.radix = radix;
    }

    // there won't be a way to decrypt, for security purposes
    public String encrypt(String middleSixDigits) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key, new FPEParameterSpec(radix, tweak));
            byte[] numeralBytes = toNumeralBytes(middleSixDigits);
            byte[] output = cipher.doFinal(numeralBytes);
            return fromNumberalBytes(output);
        } catch (GeneralSecurityException gse) {
            System.out.println(gse.getLocalizedMessage());
        }

        // return custom runtime exception when empty string is returned
        return "";
    }

    private byte[] toNumeralBytes(String digits) {
        byte[] result = new byte[digits.length()];
        for (int i = 0; i < digits.length(); i++) {
            char ch = digits.charAt(i);
            result[i] = (byte) (ch - '0');
        }
        return result;
    }

    private String fromNumberalBytes(byte[] numeralBytes) {
        StringBuilder middleSixDigits = new StringBuilder(numeralBytes.length);
        for (byte b : numeralBytes) {
            int num = b & 0xFF;
            middleSixDigits.append((char) ('0' + num));
        }
        return middleSixDigits.toString();
    }
    
}
