package com.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotFoundErrorEnum implements BaseError {
    FLEX_USER_NOT_FOUND(ErrorCodes.USER_NOT_FOUND_IM, "user_not_found"),
    USER_NOT_FOUND(ErrorCodes.USER_NOT_FOUND_IM, "user_not_found");
    private final String code;
    private final String message;
}
