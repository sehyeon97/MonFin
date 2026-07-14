package com.sehyeon.monfin.bank.dto.requests;

import java.util.UUID;

import com.sehyeon.monfin.bank.dto.responses.TransactionData;

public record VerifyOTPRequest(UUID otpID, String otp, TransactionData metaData) {}
