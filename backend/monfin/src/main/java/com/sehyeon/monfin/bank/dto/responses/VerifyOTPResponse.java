package com.sehyeon.monfin.bank.dto.responses;

public record VerifyOTPResponse(boolean verified, String authorizationCode) {}
