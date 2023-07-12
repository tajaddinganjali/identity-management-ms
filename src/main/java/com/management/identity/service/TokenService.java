package com.management.identity.service;

import com.management.identity.dto.VerificationTokenRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.util.dto.JwtUtilityDTO;

public interface TokenService {

    String generateToken(JwtUtilityDTO jwtUtility);

    String createVerificationToken(VerificationTokenRequisiteDTO verificationTokenRequisite);

    String createRegistrationToken(String userId, ConsumerTypeEnum consumerType, ProfileType profileType,
                                   String deviceId);

    String createRegistrationToken(String userId, ConsumerTypeEnum consumerType, ProfileType profileType,
                                   String deviceId, String phone);

    String createAuthorizationToken(String userId, String sessionId, ConsumerTypeEnum consumerType,
                                    ProfileType profileType);

    void verifyVerificationToken(String userId, String token);

    void verifyVerificationToken(String token);

    void verifyRegistrationToken(String userId, String token);

    void verifyAuthorizationToken(String userId, String token);

}
