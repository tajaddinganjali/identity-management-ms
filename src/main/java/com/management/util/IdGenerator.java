package com.management.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * This class specifically designed for ID generation process.
 */
@Slf4j
public final class IdGenerator {

    /**
     * Constant for base 36 (i.e., 0123456789abcdefghijklmnopqrstuvwyz) encoding
     */
    private static final int ENCODING_BASE = 36;
    /**
     * Constant that is considered for minimum time length.
     */
    private static final int TIME_LENGTH = 8;
    /**
     * Constant that is created for ip reset by getting predefined encoding base raised to the power of 2.
     */
    private static final long IP_RESET = getPower(ENCODING_BASE, 2);
    /**
     * Static field considered for keeping ip that is accustomed to class requirements.
     */
    private static final String IP = getIP();
    /**
     * Constant that is created for counter reset by getting predefined encoding base raised to the power of 3.
     */
    private static final long COUNTER_RESET = getPower(ENCODING_BASE, 3);
    /**
     * Static field considered for keeping hexadecimal representation of time.
     */
    private static String hexTime = getHexTime();
    /**
     * Static field for counter which keep track of number of times Oid is generated.
     */
    private static long counter = 0;

    /**
     * Since this class is not considered for instantiation, it throws an exception.
     */
    private IdGenerator() {
        throw new AssertionError("Do not instantiate this class, use statically.");
    }

    /**
     * Calculates and returns number raised to the power of number.
     *
     * @param number base
     * @param power  exponent
     * @return number raised to the power of number
     */
    private static long getPower(int number, int power) {
        long result = 1;
        for (int i = 0; i < power; i++) {
            result *= number;
        }
        return result;
    }

    /**
     * Returns zero padded, IP_LENGTH length, ENCODING_BASE long encoded ip
     * address.
     *
     * @return ip modified based on needs
     */
    private static String getIP() {
        long ip = 0;
        try {
            byte[] bytes = InetAddress.getLocalHost().getAddress();
            ip = (bytes[3] & 0xFF) & 0xFFFFFFFFL;
        } catch (UnknownHostException e) {
            log.error("unknown host");
        }
        return Long.toString(ip + IP_RESET, ENCODING_BASE).substring(1);
    }

    /**
     * Generate HexTime according to the milliseconds of current time and predefined <code>ENCODING_BASE</code>.
     *
     * @return hexadecimal time
     */
    private static String getHexTime() {
        /*
         * 2059'da 8 haneyi asacak ve 1980'den sonra 8 haneden eksik olmayacak
         */
        String hexTime = Long.toString(System.currentTimeMillis(), ENCODING_BASE);
        int length = hexTime.length();
        if (length > TIME_LENGTH) {
            return hexTime.substring(length - TIME_LENGTH);
        } else {
            return hexTime;
        }
    }

    /**
     * Generates and returns Object Identifier (Oid) with a combination of several static variables
     * <code>ip</code>, <code>hexTime</code>, and <code>COUNTER_RESET</code>.
     *
     * @return Object identifier
     */
    public static String generateOid() {
        synchronized (IdGenerator.class) {
            String oid = IP + hexTime + Long.toString(counter + COUNTER_RESET, ENCODING_BASE);
            counter = (counter + 1) % COUNTER_RESET;
            if (counter == 0) {
                String tempTime = getHexTime();
                while (hexTime.equalsIgnoreCase(tempTime)) {
                    tempTime = getHexTime();
                }
                hexTime = tempTime;
            }
            return oid;
        }
    }

}
