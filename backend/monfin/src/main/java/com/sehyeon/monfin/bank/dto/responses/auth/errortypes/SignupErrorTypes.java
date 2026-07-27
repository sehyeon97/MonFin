package com.sehyeon.monfin.bank.dto.responses.auth.errortypes;

/**
 * Any signup error messages should strictly exist here for cleaner code purposes.
 */
public enum SignupErrorTypes {
    PHONE_NUMBER_ALREADY_EXISTS("Phone number already in use."),
    USERNAME_ALREADY_EXISTS("Username is already taken.");

    private final String errorMessage;

    SignupErrorTypes(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
