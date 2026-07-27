package com.sehyeon.monfin.bank.dto.responses.auth;

public record SignupAndLoginAuthRes(
    String jwt,
    SignupErrorResponse signupError,
    LoginErrorResponse loginError
) {}
