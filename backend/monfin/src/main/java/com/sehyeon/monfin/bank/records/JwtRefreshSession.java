package com.sehyeon.monfin.bank.records;

import java.util.Date;
import java.util.UUID;

public record JwtRefreshSession(UUID userID, Date expiresAt) {}
