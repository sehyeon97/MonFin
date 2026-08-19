package com.sehyeon.monfin.bank.dto.responses.card;

public record BasicCardInfoResponse(
    String lastFour,
    String expMonth,
    String expYear,
    String cardTier,
    String cardNetwork,
    String cardType,
    String cardStatus,
    int monthlyLimit,
    int dailyLimit,
    int availableCredit,
    int balance,
    Boolean isDebit
) {}
