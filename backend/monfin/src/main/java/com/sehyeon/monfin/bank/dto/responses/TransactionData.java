package com.sehyeon.monfin.bank.dto.responses;

import java.time.Instant;
import java.util.UUID;

public record TransactionData(
    UUID transactionID, UUID customerID, String cardToken, UUID merchantID,
    String merchantName, String brand, String productName,
    Instant timestamp, int amount
) {}
