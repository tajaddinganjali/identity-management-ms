package com.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BadRequestErrorEnum implements BaseError {
    BAD_REQUEST(ErrorCodes.GENERAL_BAD_REQUEST, "bad_request"),
    PERMANENTLY_BLOCKED_OTP(ErrorCodes.PERMANENTLY_BLOCKED_OTP, "permanent_blocked_otp"),
    TEMPORARY_BLOCKED_OTP(ErrorCodes.TEMPORARY_BLOCKED_OTP, "temporary_blocked_otp"),
    TEMPORARY_BLOCKED_OTP_IN_MINUTE(ErrorCodes.TEMPORARY_BLOCKED_OTP_IN_MINUTES, "temporary_blocked_otp_in_minute"),
    TEMPORARY_BLOCKED_OTP_IN_HOURS(ErrorCodes.TEMPORARY_BLOCKED_OTP_IN_HOURS, "temporary_blocked_otp_in_hours"),
    PERMANENTLY_BLOCKED_SAME_PIN(ErrorCodes.TEMPORARY_BLOCKED_SAME_PIN, "permanently_blocked_same_pin"),
    TEMPORARY_BLOCKED_SAME_PIN(ErrorCodes.TEMPORARY_BLOCKED_SAME_PIN_WRONG_ATTEMPT, "temporary_blocked_same_pin"),
    PERMANENTLY_BLOCKED_SAME_PHONE(ErrorCodes.TEMPORARY_BLOCKED_SAME_PHONE_WRONG_ATTEMPT, "permanently_blocked_same_phone"),
    TEMPORARY_BLOCKED_SAME_PHONE(ErrorCodes.TEMPORARY_BLOCKED_SAME_PHONE, "temporary_blocked_same_phone"),
    PERMANENTLY_BLOCKED(ErrorCodes.PERMANENTLY_BLOCKED, "permanently_blocked"),
    PIN_PHONE_MISMATCH(ErrorCodes.PIN_PHONE_MISMATCH, "pin_phone_mismatch"),
    INVALID_CREDENTIALS(ErrorCodes.INVALID_REGISTER_CREDENTIALS_404, "invalid_credentials"),
    PIN_BIRTHDATE_MISMATCH(ErrorCodes.USER_NOT_FOUND_THIS_PIN_BIRTH_DATE, "pin_birthdate_mismatch"),
    BIRTHDATE_IS_NULL(ErrorCodes.BIRTH_DATE_CANNOT_BE_NULL, "birthdate_is_null"),
    IDENTIFIED_USER_NOT_FOUND_FLEX(ErrorCodes.IDENTIFIED_USER_NOT_FOUND_FLEX, "identified_user_not_found_flex"),
    IDENTIFIED_USER_CIF_PHONE_MISMATCH(ErrorCodes.IDENTIFIED_USER_CIF_PHONE_MISMATCH,
            "identified_user_cif_phone_mismatch"),
    DUPLICATE_PHONE_NOT_ALLOWED(ErrorCodes.DUPLICATE_PHONE_NOT_ALLOWED, "duplicate_phone"),
    USER_WITH_BLOCKED_STATUS(ErrorCodes.BLOCKED_USER_NOT_ALLOWED, "user_with_blocked_status"),
    CORPORATE_CUSTOMER(ErrorCodes.CORPORATE_CUSTOMER_NOT_ALLOWED, "corporate_customer"),
    MORE_THAN_ACCEPTED_CIF(ErrorCodes.MORE_CIF_THAN_ALLOWED, "more_than_accepted_cif"),
    INVALID_OTP_ATTEMPT(ErrorCodes.INVALID_OTP_ATTEMPT, "invalid_otp_attempt"),
    TEMPORARY_BLOCKED_SEND_SMS(ErrorCodes.TEMPORARY_BLOCKED_SEND_SMS, "temporary_blocked_send_sms"),
    LAST_SMS_FOR_TEMPORARY_BLOCK(ErrorCodes.TEMPORARY_BLOCKED_SEND_SMS, "last_sms_for_temporary_block"),
    GENERAL_BUSINESS_ERROR(ErrorCodes.GENERAL_BUSINESS_ERROR, "Successfully registration message not found by consumer."),
    LOGIN_RATE_LIMIT_EXCEED(ErrorCodes.LOGIN_RATE_LIMIT_EXCEED, "You exceed the invalid login entry limit."),
    OTP_RATE_LIMIT_EXCEED(ErrorCodes.OTP_RATE_LIMIT_EXCEED, "You exceed the invalid OTP entry limit."),
    USER_TEMPORARY_LOCKED(ErrorCodes.USER_TEMPORARY_LOCKED, "You temporary locked , because exceed the invalid OTP entry limit."),
    INVALID_TOKEN(ErrorCodes.INVALID_TOKEN, "Token is invalid or expired"),
    INVALID_CONSUMER(ErrorCodes.INVALID_CONSUMER, "The given consumer was invalid"),
    OTP_DEFINITION_NOT_FOUND(ErrorCodes.OTP_DEFINITION_NOT_FOUND, "Otp definition not found!"),
    DEVICE_CANNOT_BE_NULL(ErrorCodes.DEVICE_CANNOT_BE_NULL, "Device can not be null"),
    USER_NOT_FOUND(ErrorCodes.USER_NOT_FOUND_IM, "user_not_found"),
    INVALID_SESSION(ErrorCodes.INVALID_SESSION, "session not found"),
    DEVICE_ID_CANNOT_BE_EMPTY(ErrorCodes.DEVICE_ID_CANNOT_BE_EMPTY, "Device id cannot be empty"),
    INVALID_UUID_FORMAT(ErrorCodes.INVALID_UUID_FORMAT, "Invalid uuid format"),
    DEVICE_NOT_FOUND(ErrorCodes.DEVICE_NOT_FOUND, "Device not found with given device information"),
    DEVICE_LIMIT_EXCEEDED(ErrorCodes.DEVICE_LIMIT_EXCEEDED, "Device limit exceeded by user"),
    PHONE_ALREADY_VERIFIED(ErrorCodes.PHONE_ALREADY_VERIFIED, "Already verified Phone number"),
    INVALID_PIN(ErrorCodes.INVALID_PIN, "Invalid pin"),
    PROCESS_TIME_EXPIRED(ErrorCodes.PROCESS_TIME_EXPIRED, "Please check again"),
    MULTI_CIF_NON_RESIDENT(ErrorCodes.MULTI_CIF_NON_RESIDENT_NOT_ALLOWED, "multi_cif_non_resident");
    private final String code;
    private final String message;
}
