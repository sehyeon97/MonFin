package com.sehyeon.monfin.bank.dto.requests;

import java.time.Instant;
import java.util.UUID;

/**
 * Request from payment processor to the customer's (issuer's) bank
 * and charge a customer then forward the money into the merchant's account
 * The cryptogram will be HMAC-generated, and it takes into account:
 * cardToken, merchantID, timestamp, and amount
 * Transaction ID is shared between Payment Processor and the Bank
 */
public record CardAuthorizationRequest(
    UUID transactionID, String cardToken, UUID merchantID, String merchantName,
    Instant timestamp, int amount, String cryptogram, String callbackUrl) {}
