package com.management.otp.service;

import com.management.model.FixOtpDefinition;
import com.management.model.OtpDefinition;
import com.management.model.User;
import com.management.otp.dto.UserCifPhoneMapDTO;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.dto.internal.ReferralInfoDTO;
import com.management.register.enums.KafkaEventType;
import com.management.register.enums.TokenType;
import java.util.Optional;

public interface CacheService {

    Optional<FixOtpDefinition> findFixOtpDefinition(String pin);

    OtpDefinition findOtpDefinition(String id);

    boolean hasKey(String token);

    void delete(String key);

    Object getValue(String value);

    void setValue(String token, RegisterCacheDTO registerCache, Integer expireTimeMinutes);

    String createTOTP(String key);

    String getSecret(String key);

    boolean validateTOTP(String otp, String secret);

    void saveToken(String userId, TokenType tokenType, String token);

    void verifyToken(String userId, TokenType tokenType, String token);

    void addUserToCache(User user);

    void saveUserPhoneCifs(String userId,
                           String verificationToken,
                           UserCifPhoneMapDTO userCifPhoneMapDTO);

    UserCifPhoneMapDTO getUserPhoneCifs(String userId, String verificationToken);

    void saveReferralToCache(String userId, ReferralInfoDTO referral);

    void deleteUserFromCache(String userId);

    void saveRegistrationTypeToCache(String userId, String deviceId,
                                     String consumerId, KafkaEventType kafkaEventType);

}
