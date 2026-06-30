package com.sehyeon.monfin.bank.dto.requests;

import java.util.UUID;

public record VerifyOTPRequest(UUID transactionID, String otp) {}
