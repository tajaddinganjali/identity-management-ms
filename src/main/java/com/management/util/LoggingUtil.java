package com.management.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;

public final class LoggingUtil {

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    private LoggingUtil() {
        throw new IllegalStateException("can not instantiate");
    }

    public static Set<Class<?>> getWrapperTypes() {

        Set<Class<?>> wrappers = new HashSet<>();
        wrappers.add(Boolean.class);
        wrappers.add(Character.class);
        wrappers.add(Byte.class);
        wrappers.add(Short.class);
        wrappers.add(Integer.class);
        wrappers.add(Long.class);
        wrappers.add(Float.class);
        wrappers.add(Double.class);
        wrappers.add(Void.class);
        wrappers.add(String.class);
        return wrappers;
    }


    /**
     * Checks whether predefined class is <code>Wrapper</code> type or not.
     *
     * @param clazz class that is required to be analyzed
     * @return whether class is Wrapper Type or not
     */
    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String groupString(String arg1, String... args) {

        StringJoiner joiner = new StringJoiner("_");
        if (Objects.nonNull(arg1)) {
            joiner.add(arg1);
        }
        joiner.add(StringUtils.joinWith("_", args));
        return joiner.toString().toLowerCase();
    }

}
