package com.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GeneralTechErrorEnum implements BaseError {
    SERVER_ERROR(ErrorCodes.SERVER_ERROR, "server_error"),
    GENERAL_SERVER_ERROR(ErrorCodes.GENERAL_TECHNICAL_ERROR, "general_server_error"),
    GATEWAY_TIMEOUT(ErrorCodes.GETAWAY_TIMEOUT, "gateway_timeout");

    private final String code;
    private final String message;
}
