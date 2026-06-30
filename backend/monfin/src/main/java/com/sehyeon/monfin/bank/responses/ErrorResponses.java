package com.sehyeon.monfin.bank.responses;

import java.time.Instant;

/**
 * Spring Boot automatically serializes code, message, and timestamp into a json,
 * so that it is more readable
 */
public class ErrorResponses {
    
    private final int code;
    private final String message;
    private final Instant timestamp;

    public ErrorResponses(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public int getErrorCode() {
        return code;
    }

    public String getErrorMessage() {
        return message;
    }

    public Instant getErrorTimestamp() {
        return timestamp;
    }

}
