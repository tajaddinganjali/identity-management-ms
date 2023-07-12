package com.management.util;

import com.management.register.enums.ConsumerTypeEnum;
import com.management.util.dto.JwtUtilityDTO;

public interface IJwtUtility {

    String createAccessTokenFoWeb(JwtUtilityDTO jwtUtility);

    String createAccessTokenForMobile(JwtUtilityDTO jwtUtility);

    String createRefreshTokenForWeb(String userId, String sessionId, ConsumerTypeEnum consumerTypeEnum);

    String createRefreshTokenForMobile(JwtUtilityDTO jwtUtility);

    String getUserIdFromToken(String token);

    String getDeviceIdFromToken(String token);

    String getSessionIdFromToken(String token);

    String getConsumerIdFromToken(String token);

    Boolean isExpired(String token);

}
