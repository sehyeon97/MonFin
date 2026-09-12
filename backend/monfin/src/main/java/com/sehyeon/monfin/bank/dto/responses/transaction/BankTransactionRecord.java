package com.sehyeon.monfin.bank.dto.responses.transaction;

import java.time.Instant;
import java.util.UUID;

public record BankTransactionRecord(
    UUID transactionID, String lastFour,
    String merchantName,
    Instant timestamp, String amount, String status
) {}
