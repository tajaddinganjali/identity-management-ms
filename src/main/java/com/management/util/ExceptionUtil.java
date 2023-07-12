package com.management.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibam.errorhandling.IBAMBaseException;
import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.ErrorResponseDTO;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.logging.CustomFeignLogging;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public final class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException("can not instantiate");
    }

    public static Exception handleFeignCommonException(String methodKey, Response response) {
        int responseStatus = response.status();
        String responseBody = CustomFeignLogging.getResponseAsText(response);
        return handleCommonException(methodKey, responseStatus, responseBody);
    }

    public static Exception handleWebClientCommonException(String method, Integer responseStatus, String responseBody) {
        return handleCommonException(method, responseStatus, responseBody);
    }

    private static IBAMBaseException handleCommonException(String methodKey, Integer statusCode,
                                                           String responseBody) {
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        log.error(responseBody);
        boolean isTimeout = statusCode == 504;
        boolean isBadRequest = statusCode >= 400 && statusCode < 500;
        if (isBadRequest) {
            return new BadRequestException(BadRequestErrorEnum.BAD_REQUEST, methodKey, httpStatus, responseBody);
        } else if (isTimeout) {
            return new GeneralTechnicalException(GeneralTechErrorEnum.GATEWAY_TIMEOUT, methodKey, httpStatus);
        } else {
            return new GeneralTechnicalException(GeneralTechErrorEnum.SERVER_ERROR, methodKey, httpStatus);
        }
    }

    public static Exception handleOtpException(String method, Integer responseStatus, String responseBody) {
        if (HttpStatus.resolve(responseStatus) == HttpStatus.BAD_REQUEST) {
            try {
                ErrorResponseDTO errorResponse = UserUtil.OBJECT_MAPPER_SNAKE_CASE.readValue(responseBody,
                        ErrorResponseDTO.class);
                if (errorResponse.getErrorCode().equalsIgnoreCase("1")) {
                    throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_SEND_SMS);
                }
                throw new BadRequestException(BadRequestErrorEnum.BAD_REQUEST, responseBody);
            } catch (JsonProcessingException e) {
                throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "can_not_parse");
            }

        }
        return handleCommonException(method, responseStatus, responseBody);
    }


    public static void throwOtpLimitException(boolean isLastPerRoundWrongAttempt, boolean isLastWrongRoundAttempt,
                                              int availableAttemptCount, int lockTimeInMinute) {
        if (isLastPerRoundWrongAttempt && isLastWrongRoundAttempt) {
            throw new BadRequestException(BadRequestErrorEnum.PERMANENTLY_BLOCKED_OTP,
                    LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP.toString());
        } else if (isLastPerRoundWrongAttempt) {
            boolean isMinute = lockTimeInMinute < 60;
            if (isMinute) {
                throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_OTP_IN_MINUTE, lockTimeInMinute);
            }
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_OTP_IN_HOURS, lockTimeInMinute / 60);
        } else {
            throw new BadRequestException(BadRequestErrorEnum.INVALID_OTP_ATTEMPT, availableAttemptCount);
        }
    }

    public static void throwRegisterSamePinLimitException(boolean isLastWrongAttempt) {
        if (isLastWrongAttempt) {
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_SAME_PIN,
                    LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN.toString());
        }
    }

    public static void throwRegisterSamePhoneLimitException(boolean isLastWrongAttempt) {
        if (isLastWrongAttempt) {
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_SAME_PHONE,
                    LimitServiceTypeEnum.REGISTER_WITH_SAME_PHONE.toString());
        }
    }

    public static void throwAuthorizationSmsLimitException(boolean hasOneSmsLimit) {
        if (hasOneSmsLimit) {
            throw new BadRequestException(BadRequestErrorEnum.LAST_SMS_FOR_TEMPORARY_BLOCK,
                    LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS.toString());
        }
    }

}
