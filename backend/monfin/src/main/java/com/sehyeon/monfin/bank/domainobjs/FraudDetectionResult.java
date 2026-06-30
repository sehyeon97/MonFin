package com.sehyeon.monfin.bank.domainobjs;

import java.util.List;

public record FraudDetectionResult(int riskScore, List<String> detectedFrauds, boolean passed) {}
