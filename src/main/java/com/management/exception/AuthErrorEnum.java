package com.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthErrorEnum implements BaseError {
    UNAUTHORIZED(ErrorCodes.UNAUTHORIZED, "unauthorized");
    private final String code;
    private final String message;
}
