package com.management.exception;

import com.ibam.errorhandling.IBAMBaseException;
import com.management.util.LoggingUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralTechnicalException extends IBAMBaseException {

    private static final String ERROR_MESSAGE = "A technical error occurred while processing the request.";
    private static final long serialVersionUID = -4553214354083669672L;
    private HttpStatus httpStatus;

    public GeneralTechnicalException() {
        super(GeneralTechErrorEnum.GENERAL_SERVER_ERROR.getCode(), ERROR_MESSAGE);
    }


    public GeneralTechnicalException(BaseError baseError) {
        super(baseError.getCode(), baseError.getMessage());
    }

    public GeneralTechnicalException(String exceptionMessage) {
        super(GeneralTechErrorEnum.GENERAL_SERVER_ERROR.getCode(), exceptionMessage);
    }

    public GeneralTechnicalException(BaseError baseError, String exceptionMessage) {
        super(baseError.getCode(), LoggingUtil.groupString(exceptionMessage, baseError.getMessage()));
    }

    public GeneralTechnicalException(BaseError baseError, String exceptionMessage, HttpStatus httpStatus) {
        super(baseError.getCode(), LoggingUtil.groupString(exceptionMessage, baseError.getMessage()));
        this.httpStatus = httpStatus;
    }

    public GeneralTechnicalException(BaseError baseError, String exceptionMessage, Throwable cause) {
        super(baseError.getCode(), LoggingUtil.groupString(exceptionMessage, baseError.getMessage()), cause);
    }

    public GeneralTechnicalException(GeneralTechErrorEnum generalServerError, Throwable throwable) {
        super(generalServerError.getCode(), generalServerError.getMessage(), throwable);
    }

}
