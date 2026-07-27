package com.sehyeon.monfin.bank.dto.responses.transaction;

public record TransactionResponse(
    TransactionData transactionData, CardAuthorizationResponse resData
) {}
