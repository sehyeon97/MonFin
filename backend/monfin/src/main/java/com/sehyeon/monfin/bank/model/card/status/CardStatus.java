package com.sehyeon.monfin.bank.model.card.status;

/**
 * Issued: card is given to user but not activated, therefore cannot use card
 * Active: card can be used
 * Frozen: user freezed card / fraud suspiscion / replaced / stolen / lost / suspended,
 *          therefore cannot use card
 * Closed: user cancels card / replacement issued / account closed
 */
public enum CardStatus {
    ISSUED, ACTIVE, FROZEN, CLOSED;
}
