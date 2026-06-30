package com.sehyeon.monfin.bank.dto.responses;

public record CardAuthorizationResponse(
    boolean authorized, String authorizationCode, String declineReason, String bankCallbackUrl) {}
