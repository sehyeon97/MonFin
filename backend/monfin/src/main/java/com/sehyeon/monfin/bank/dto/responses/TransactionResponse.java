package com.sehyeon.monfin.bank.dto.responses;

public record TransactionResponse(
    TransactionData transactionData, CardAuthorizationResponse resData
) {}
