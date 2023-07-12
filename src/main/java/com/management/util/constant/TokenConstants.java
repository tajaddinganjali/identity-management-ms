package com.management.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenConstants {

    public static final int MOBILE_ACCESS_TOKEN_EXPIRE_TIME_SECS = 180; // 180 seconds (3 minutes)
    public static final int MOB_REFRESH_TOKEN_EXPIRE_TIME_SECS = 360; // 360 seconds (6 minutes)

    public static final int WEB_ACCESS_TOKEN_EXPIRE_TIME_IN_SECONDS = 600; // 600 seconds (10 minutes)
    public static final int WEB_REFRESH_TOKEN_EXPIRE_TIME_IN_SECONDS = 780; // 780 seconds (13 minutes)

    public static final String VERIFICATION_TOKEN_TYPE = "vt";
    public static final String REGISTRATION_TOKEN_TYPE = "rt";
    public static final String AUTHORIZATION_TOKEN_TYPE = "at";
    public static final String JWT_PREFIX = "Bearer ";

}
