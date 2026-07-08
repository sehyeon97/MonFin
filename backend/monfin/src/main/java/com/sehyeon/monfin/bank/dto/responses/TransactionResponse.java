package com.sehyeon.monfin.bank.dto.responses;

import java.util.UUID;

public record TransactionResponse(UUID transactionID, CardAuthorizationResponse resData) {}
