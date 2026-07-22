package com.sehyeon.monfin.bank.dto.requests;

public record CardTokenizationRequest(
    String pan,
    String cvv,
    String fullName,
    String expMonth,
    String expYear,
    String userID
) {}
