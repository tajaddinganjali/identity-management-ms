package com.management.exception;

import com.ibam.errorhandling.IBAMBaseException;
import com.management.util.LoggingUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class AuthException extends IBAMBaseException {

    private static final long serialVersionUID = -8174675048475436044L;

    private HttpStatus httpStatus;
    private String responseBody;

    public AuthException(BaseError baseError) {
        super(baseError.getCode(), baseError.getMessage());
    }

    public AuthException(BaseError baseError, String exceptionMessage, Throwable cause) {
        super(baseError.getCode(), exceptionMessage, cause);
    }

    public AuthException(BaseError baseError, String exceptionMessage) {
        super(baseError.getCode(), LoggingUtil.groupString(baseError.getMessage(), exceptionMessage));
    }

    public AuthException(BaseError baseError, String exceptionMessage, HttpStatus httpStatus,
                         String responseBody) {
        super(baseError.getCode(), LoggingUtil.groupString(baseError.getMessage(), exceptionMessage));
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

}
