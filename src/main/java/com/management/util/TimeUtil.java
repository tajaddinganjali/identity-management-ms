package com.management.util;


import com.management.exception.GeneralTechnicalException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TimeUtil {

    private TimeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getFormattedDateTime(LocalDateTime dateTime, String pattern) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);

        return dateTimeFormatter.format(dateTime);
    }

    public static String convertFormat(String date, String currentformat, String newFormat) {
        SimpleDateFormat sdfCurrent = new SimpleDateFormat(currentformat);
        SimpleDateFormat sdfNew = new SimpleDateFormat(newFormat);
        try {
            return sdfNew.format(sdfCurrent.parse(date));
        } catch (ParseException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    public static ZonedDateTime convertUTCToZoneDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
        ZonedDateTime zonedDateTime;
        try {
            OffsetDateTime timeUtc = localDateTime.atOffset(ZoneOffset.UTC);
            OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(zoneId.getRules().getOffset(localDateTime));
            zonedDateTime = offsetTime.toZonedDateTime();
        } catch (DateTimeException e) {
            throw new GeneralTechnicalException(e.getMessage());
        }
        return zonedDateTime;
    }

    public static Date getCurrentDateTime() {
        return new Date();
    }

}
