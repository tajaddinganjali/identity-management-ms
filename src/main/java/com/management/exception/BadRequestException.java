package com.management.exception;

import com.ibam.errorhandling.IBAMBaseException;
import com.management.util.LoggingUtil;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class BadRequestException extends IBAMBaseException {

    private static final long serialVersionUID = -8174675048475436044L;

    private HttpStatus httpStatus;
    private String responseBody;

    public BadRequestException(BaseError baseError) {
        super(baseError.getCode(), baseError.getMessage());
    }

    public BadRequestException(BaseError baseError, Object... args) {
        super(baseError.getCode(), baseError.getMessage(), args);
    }

    public BadRequestException(BaseError baseError, String exceptionMessage, Throwable cause) {
        super(baseError.getCode(), exceptionMessage, cause);
    }

    public BadRequestException(BaseError baseError, String exceptionMessage, Object... args) {
        super(baseError.getCode(), LoggingUtil.groupString(baseError.getMessage(), exceptionMessage), args);
    }

    public BadRequestException(BaseError baseError, String exceptionMessage, HttpStatus httpStatus,
                               String responseBody) {
        super(baseError.getCode(), LoggingUtil.groupString(baseError.getMessage(), exceptionMessage));
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public BadRequestException(BaseError baseError, String exceptionMessage, Map<String, Object> data) {
        super(baseError.getCode(), exceptionMessage, null, data);
    }

    public BadRequestException(BaseError baseError, String exceptionMessage, Map<String, Object> data, Object... args) {
        super(baseError.getCode(), exceptionMessage, null, data, args);
    }

}
