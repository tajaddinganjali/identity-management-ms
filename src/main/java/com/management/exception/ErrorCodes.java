package com.management.exception;

public final class ErrorCodes {

    /* BUSINESS ERRORS */
    public static final String PIN_PHONE_MISMATCH = "IBAM-IAM-BIZ-0004";
    public static final String INVALID_OTP_ATTEMPT = "IBAM-IM-BIZ-0014";
    public static final String INVALID_TOKEN = "IBAM-IAM-BIZ-0012";
    public static final String OTP_RATE_LIMIT_EXCEED = "IBAM-IAM-BIZ-0013";
    public static final String INVALID_SESSION = "IBAM-IAM-BIZ-0015";
    public static final String USER_TEMPORARY_LOCKED = "IBAM-IAM-BIZ-0016";
    public static final String INVALID_PASSCODE = "IBAM-IAM-BIZ-0017";
    public static final String LOGIN_RATE_LIMIT_EXCEED = "IBAM-IAM-BIZ-0019";
    public static final String INVALID_PIN = "IBAM-IAM-BIZ-0021";
    public static final String BIRTH_DATE_CANNOT_BE_NULL = "IBAM-IAM-BIZ-0025";
    public static final String PHONE_ALREADY_VERIFIED = "IBAM-IAM-BIZ-0026";
    public static final String OTP_DEFINITION_NOT_FOUND = "IBAM-IAM-BIZ-0028";
    public static final String BLOCKED_USER_NOT_ALLOWED = "IBAM-IAM-BIZ-0029";
    public static final String DEVICE_CANNOT_BE_NULL = "IBAM-IAM-BIZ-0033";
    public static final String DEVICE_NOT_FOUND = "IBAM-IAM-BIZ-0034";
    public static final String INVALID_CONSUMER = "IBAM-IAM-BIZ-0035";
    public static final String DEVICE_ID_CANNOT_BE_EMPTY = "IBAM-IAM-BIZ-0037";
    public static final String INVALID_UUID_FORMAT = "IBAM-IAM-BIZ-0038";
    public static final String DEVICE_LIMIT_EXCEEDED = "IBAM-IAM-BIZ-0039";
    public static final String PROCESS_TIME_EXPIRED = "IBAM-IAM-BIZ-0044";
    public static final String INVALID_REGISTER_CREDENTIALS_404 = "IBAM-IAM-BIZ-0054";
    public static final String UNAUTHORIZED = "IBAM-IAM-TECH-0001";

    /* TECHNICAL ERRORS */
    public static final String GENERAL_TECHNICAL_ERROR = "IBAM-GENERAL-TECH-0001";
    public static final String GENERAL_BUSINESS_ERROR = "IBAM-GENERAL-BIZ-0001";
    public static final String SERVER_ERROR = "IBAM-IAM-GENERAL-TECH-503";
    public static final String GETAWAY_TIMEOUT = "IBAM-IAM-GENERAL-TECH-504";
    public static final String USER_NOT_FOUND_IM = "IBAM-IM-BIZ-002";
    public static final String GENERAL_BAD_REQUEST = "IBAM-IM-BIZ-001";
    public static final String TEMPORARY_BLOCKED_OTP = "IBAM-IM-BIZ-0002";
    public static final String PERMANENTLY_BLOCKED_OTP = "IBAM-IM-BIZ-0003";
    public static final String TEMPORARY_BLOCKED_SAME_PIN = "IBAM-IM-BIZ-0004";
    public static final String TEMPORARY_BLOCKED_SAME_PIN_WRONG_ATTEMPT = "IBAM-IM-BIZ-0005";
    public static final String TEMPORARY_BLOCKED_SAME_PHONE_WRONG_ATTEMPT = "IBAM-IM-BIZ-0006";
    public static final String TEMPORARY_BLOCKED_SAME_PHONE = "IBAM-IM-BIZ-0007";
    public static final String PERMANENTLY_BLOCKED = "IBAM-IM-BIZ-0008";
    public static final String INVALID_PHONE_NUMBER_FORMAT_IM = "IBAM-IM-BIZ-VALIDATION-0001";
    public static final String INVALID_OTP_FORMAT_IM = "IBAM-IM-BIZ-VALIDATION-0002";
    public static final String IDENTIFIED_USER_NOT_FOUND_FLEX = "IBAM-IM-BIZ-0009";
    public static final String IDENTIFIED_USER_CIF_PHONE_MISMATCH = "IBAM-IM-BIZ-0010";
    public static final String USER_NOT_FOUND_THIS_PIN_BIRTH_DATE = "IBAM-IM-BIZ-0011";
    public static final String TEMPORARY_BLOCKED_OTP_IN_MINUTES = "IBAM-IM-BIZ-0012";
    public static final String TEMPORARY_BLOCKED_OTP_IN_HOURS = "IBAM-IM-BIZ-0013";
    public static final String TEMPORARY_BLOCKED_SEND_SMS = "IBAM-SMS-BIZ-0002";
    public static final String DUPLICATE_PHONE_NOT_ALLOWED = "IBAM-IM-BIZ-0015";
    public static final String CORPORATE_CUSTOMER_NOT_ALLOWED = "IBAM-IM-BIZ-0016";
    public static final String MORE_CIF_THAN_ALLOWED = "IBAM-IM-BIZ-0017";
    public static final String MULTI_CIF_NON_RESIDENT_NOT_ALLOWED = "IBAM-IAM-BIZ-0058";


    /* VALIDATION ERRORS */
    public static final String INVALID_PIN_FORMAT_IM = "IBAM-IM-BIZ-VALIDATION-0002";

    private ErrorCodes() {

    }

}
