package com.management.util;

import com.management.logging.Log;
import java.util.Arrays;
import java.util.List;

/**
 * This class dedicated to handle operations that are required for logging functionality
 * and can be called from any part of the microservice.
 *
 * <p>This class is not considered to be instantiated; therefore, all methods were set <code>static</code>.
 */
public final class LogUtil {

    public static final List<String> EXCLUSIONS = Arrays.asList("authorization", "verificationToken", "accessToken",
            "refreshToken", "authorizationToken", "authorizationHeader", "cardNumber", "registrationToken",
            "otp", "passcode", "pin", "cifs", "dateOfBirth", "idIssuingDate", "maturityDate", "idNumber");


    /**
     * Throws <code>IllegalStateException</code> since this class is not considered for instantiation.
     *
     * @throws IllegalStateException while class instantiation.
     */
    private LogUtil() {
        throw new AssertionError("Do not instantiate this class, use statically.");
    }


    /**
     * Fetches <code>Log</code> annotation from class.
     * If annotation is not found, returns {@code null}.
     *
     * @param clazz class that is requested to be searched for annotation
     * @return annotation that is found
     */
    public static Log getLogAnnotationFromClass(Class<?> clazz) {

        Log[] logAnnotations = clazz.getAnnotationsByType(Log.class);
        Log logAnnotation = null;

        if (logAnnotations != null && logAnnotations.length != 0) {
            logAnnotation = logAnnotations[0];
        }
        return logAnnotation;
    }

    /**
     * Converts regular text format to JSON format.
     *
     * @param message text that is requested to be converted
     * @return text in JSON format
     */
    public static String formatLogMessageAsJson(String message) {
        return String.format("{\"message\": \"%s\"}", message);
    }

}
