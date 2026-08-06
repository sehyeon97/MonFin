package com.sehyeon.monfin.bank.security;

public enum LoginStatus {

    SUCCESS("Login successful."), FAIL("Login failed.");

    private final String message;

    private LoginStatus(String message) {
        this.message = message;
    }

    public String getLoginStatusMessage() {
        return message;
    }
    
}
