package com.sehyeon.monfin.bank.security;

public enum SignupStatus {
    SUCCESS("Signup successful."), FAIL("Signup failed.");

    private final String message;

    private SignupStatus(String message) {
        this.message = message;
    }

    public String getSignupStatusMessage() {
        return message;
    }
}
