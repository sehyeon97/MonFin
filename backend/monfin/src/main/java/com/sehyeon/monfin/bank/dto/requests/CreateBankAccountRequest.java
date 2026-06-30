package com.sehyeon.monfin.bank.dto.requests;

public record CreateBankAccountRequest(String username, String password, String fullName, String phoneNumber) {}
