package com.sehyeon.monfin.bank.dto.responses.auth;

import com.sehyeon.monfin.bank.model.entity.bank.BankAccount;

public record SignupAndLoginAuthRes(
    BankAccount bankAccount,
    SignupErrorResponse signupError,
    LoginErrorResponse loginError
) {}
