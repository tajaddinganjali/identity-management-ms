package com.management.service;

import com.management.exception.BadRequestException;
import com.management.identity.dto.VerificationTokenRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.impl.TokenServiceImpl;
import com.management.otp.service.CacheService;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.util.JwtTokenUtil;
import com.management.util.dto.JwtUtilityDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenServiceUnitTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void generateToken() {
        JwtUtilityDTO jwtUtilityDTO = JwtUtilityDTO.builder()
                .tokenType("token")
                .build();
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("token");
        Assertions.assertDoesNotThrow(() -> tokenService.generateToken(jwtUtilityDTO));
    }

    @Test
    void createVerificationToken() {
        VerificationTokenRequisiteDTO verificationTokenRequisiteDTO = VerificationTokenRequisiteDTO.builder()
                .userId("id")
                .consumer(ConsumerTypeEnum.MOBILE)
                .profileType(ProfileType.CUSTOMER)
                .deviceId("id")
                .phone("phone")
                .build();
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("token");
        Mockito.doNothing().when(cacheService).saveToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.createVerificationToken(verificationTokenRequisiteDTO));
    }

    @Test
    void createRegistrationToken() {
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("token");
        Mockito.doNothing().when(cacheService).saveToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.createRegistrationToken("id",
                ConsumerTypeEnum.MOBILE, ProfileType.CUSTOMER, "id"));
    }

    @Test
    void createRegistrationTokenn() {
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("token");
        Mockito.doNothing().when(cacheService).saveToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.createRegistrationToken("id",
                ConsumerTypeEnum.MOBILE, ProfileType.CUSTOMER, "id", "phone"));
    }

    @Test
    void createAuthorizationToken() {
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("token");
        Mockito.doNothing().when(cacheService).saveToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.createAuthorizationToken("id", "id",
                ConsumerTypeEnum.MOBILE, ProfileType.CUSTOMER));
    }

    @Test
    void verifyVerificationToken() {
        Mockito.when(jwtTokenUtil.validateToken(Mockito.anyString())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> tokenService.verifyVerificationToken("token"));
    }

    @Test
    void verifyVerificationToken_exception() {
        Mockito.when(jwtTokenUtil.validateToken(Mockito.anyString())).thenThrow(BadRequestException.class);
        Assertions.assertThrows(BadRequestException.class, () -> tokenService.verifyVerificationToken(""));
    }

    @Test
    void verifyVerificationTokenn() {
        Mockito.doNothing().when(cacheService).verifyToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.verifyVerificationToken("userId", "token"));
    }

    @Test
    void verifyRegistrationToken() {
        Mockito.doNothing().when(cacheService).verifyToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.verifyRegistrationToken("userId", "token"));
    }

    @Test
    void verifyAuthorizationToken() {
        Mockito.doNothing().when(cacheService).verifyToken(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Assertions.assertDoesNotThrow(() -> tokenService.verifyAuthorizationToken("userId", "token"));
    }

}
