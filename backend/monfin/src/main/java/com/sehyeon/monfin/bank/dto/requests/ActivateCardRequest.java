package com.sehyeon.monfin.bank.dto.requests;

public record ActivateCardRequest(String lastFour, String cardType, String cardNetwork, String cardTier) {}
