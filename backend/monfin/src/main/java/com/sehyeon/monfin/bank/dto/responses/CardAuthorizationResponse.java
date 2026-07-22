package com.sehyeon.monfin.bank.dto.responses;

import java.util.UUID;

public record CardAuthorizationResponse(
    boolean authorized, String authorizationCode, String declineReason, String url, UUID otpID) {}
