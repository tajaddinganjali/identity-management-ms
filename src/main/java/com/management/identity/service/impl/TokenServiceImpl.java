package com.management.identity.service.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.identity.dto.VerificationTokenRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.TokenService;
import com.management.otp.service.CacheService;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.TokenType;
import com.management.util.JwtTokenUtil;
import com.management.util.constant.TokenConstants;
import com.management.util.dto.JwtUtilityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private static final String LOG_MESSAGE = "generating %s token.";
    private static final String EMPTY_UUID_STR = "00000000-0000-0000-0000-000000000000";
    private final JwtTokenUtil jwtTokenUtil;
    private final CacheService cacheService;

    @Override
    public String generateToken(JwtUtilityDTO jwtUtility) {
        return jwtTokenUtil.generateToken(jwtUtility);
    }

    @Override
    public String createVerificationToken(VerificationTokenRequisiteDTO verificationTokenRequisite) {

        TokenType tokenType = TokenType.VERIFICATION_TOKEN;
        log.info(String.format(LOG_MESSAGE, tokenType));
        String token = jwtTokenUtil.generateToken(JwtUtilityDTO.builder()
                .subject(verificationTokenRequisite.getUserId())
                .sessionId(EMPTY_UUID_STR)
                .consumerTypeEnum(verificationTokenRequisite.getConsumer())
                .profileType(verificationTokenRequisite.getProfileType())
                .deviceId(verificationTokenRequisite.getDeviceId())
                .userId(verificationTokenRequisite.getUserId())
                .tokenType(TokenConstants.VERIFICATION_TOKEN_TYPE)
                .phone(verificationTokenRequisite.getPhone()).build());

        cacheService.saveToken(verificationTokenRequisite.getUserId(), tokenType, token);

        return token;
    }

    @Override
    public String createRegistrationToken(String userId, ConsumerTypeEnum consumerType, ProfileType profileType,
                                          String deviceId) {
        TokenType tokenType = TokenType.REGISTRATION_TOKEN;
        log.info(String.format(LOG_MESSAGE, tokenType));
        String token = jwtTokenUtil.generateToken(JwtUtilityDTO.builder()
                .subject(userId)
                .sessionId(EMPTY_UUID_STR)
                .consumerTypeEnum(consumerType)
                .profileType(profileType)
                .deviceId(deviceId)
                .tokenType(TokenConstants.REGISTRATION_TOKEN_TYPE)
                .build());

        cacheService.saveToken(userId, tokenType, token);

        return token;
    }

    @Override
    public String createRegistrationToken(String userId, ConsumerTypeEnum consumerType, ProfileType profileType,
                                          String deviceId,
                                          String phone) {
        TokenType tokenType = TokenType.REGISTRATION_TOKEN;
        log.info(String.format(LOG_MESSAGE, tokenType));
        String token = jwtTokenUtil.generateToken(JwtUtilityDTO.builder()
                .subject(userId)
                .sessionId(EMPTY_UUID_STR)
                .consumerTypeEnum(consumerType)
                .profileType(profileType)
                .deviceId(deviceId)
                .tokenType(TokenConstants.REGISTRATION_TOKEN_TYPE)
                .phone(phone).build());

        cacheService.saveToken(userId, tokenType, token);

        return token;
    }

    @Override
    public String createAuthorizationToken(String userId,
                                           String sessionId, ConsumerTypeEnum consumerType, ProfileType profileType) {
        TokenType tokenType = TokenType.AUTHORIZATION_TOKEN;
        log.info(String.format(LOG_MESSAGE, tokenType));
        String token = jwtTokenUtil.generateToken(JwtUtilityDTO.builder()
                .subject(userId)
                .sessionId(sessionId)
                .consumerTypeEnum(consumerType)
                .profileType(profileType)
                .deviceId("")
                .tokenType(TokenConstants.AUTHORIZATION_TOKEN_TYPE)
                .phone(null).build());

        cacheService.saveToken(userId, tokenType, token);

        return token;
    }

    @Override
    public void verifyVerificationToken(String userId, String token) {
        TokenType tokenType = TokenType.VERIFICATION_TOKEN;
        cacheService.verifyToken(userId, tokenType, token);
    }

    @Override
    public void verifyVerificationToken(String token) {
        try {
            jwtTokenUtil.validateToken(token);
        } catch (Exception exception) {
            throw new BadRequestException(BadRequestErrorEnum.BAD_REQUEST, "token_not_verified");
        }
    }

    @Override
    public void verifyRegistrationToken(String userId, String token) {
        TokenType tokenType = TokenType.REGISTRATION_TOKEN;
        cacheService.verifyToken(userId, tokenType, token);
    }

    @Override
    public void verifyAuthorizationToken(String userId, String token) {
        TokenType tokenType = TokenType.AUTHORIZATION_TOKEN;
        cacheService.verifyToken(userId, tokenType, token);
    }

}
