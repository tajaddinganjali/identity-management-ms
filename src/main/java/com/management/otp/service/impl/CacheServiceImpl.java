package com.management.otp.service.impl;

import static java.util.concurrent.TimeUnit.MINUTES;

import com.google.common.hash.Hashing;
import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.model.FixOtpDefinition;
import com.management.model.OtpDefinition;
import com.management.model.User;
import com.management.otp.dto.UserCifPhoneMapDTO;
import com.management.otp.service.CacheService;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.dto.internal.ReferralInfoDTO;
import com.management.register.enums.KafkaEventType;
import com.management.register.enums.TokenType;
import com.management.repository.FixOtpDefinitionRepository;
import com.management.repository.OtpDefinitionRepository;
import com.management.util.TimeBasedOTP;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheServiceImpl implements CacheService {

    private static final String SALT = "wteueirpkrr.rjbdjndjnr-dbjdndjr";
    private static final String KEY_VALUE_FORMATTER = "%s_%s";
    private final FixOtpDefinitionRepository fixOtpDefinitionRepository;
    private final OtpDefinitionRepository otpDefinitionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RMapCache<String, User> userCache;
    private final TimeBasedOTP timeBasedOTP;

    public CacheServiceImpl(FixOtpDefinitionRepository fixOtpDefinitionRepository,
                            @Qualifier("customJacksonCodec") TypedJsonJacksonCodec typedJsonJacksonCodec,
                            OtpDefinitionRepository otpDefinitionRepository, RedissonClient redissonClient,
                            @Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                            @Qualifier("customTimeBasedOtp") TimeBasedOTP timeBasedOTP) {
        this.fixOtpDefinitionRepository = fixOtpDefinitionRepository;
        this.otpDefinitionRepository = otpDefinitionRepository;
        this.redisTemplate = redisTemplate;
        this.timeBasedOTP = timeBasedOTP;
        this.userCache = redissonClient.getMapCache("ibam_users_v5", typedJsonJacksonCodec);
    }


    @Override
    @Cacheable(value = "findFixOtpDefinition", unless = "#result == null")
    public Optional<FixOtpDefinition> findFixOtpDefinition(String pin) {
        return fixOtpDefinitionRepository.findFirstByPin(pin);
    }

    @Override
    @Cacheable("findOtpDefinition")
    public OtpDefinition findOtpDefinition(String id) {
        return otpDefinitionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException(BadRequestErrorEnum.OTP_DEFINITION_NOT_FOUND));
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Object getValue(String value) {
        return redisTemplate.opsForValue().get(value);
    }

    @Override
    public void setValue(String token, RegisterCacheDTO registerCache, Integer expireTimeMinutes) {
        redisTemplate.opsForValue().set(token, registerCache, Duration.ofMinutes(expireTimeMinutes));
    }

    @Override
    public String createTOTP(String key) {
        String secret = getSecret(key);
        return timeBasedOTP.generateTOTP(secret);
    }

    @Override
    public String getSecret(String key) {
        return key.concat(SALT);
    }

    /**
     * we use findFixOtpDefinition
     * in test environment to enable testing
     * in prod environment to enable testing for stores
     */
    @Override
    public boolean validateTOTP(String otp, String secret) {
        Optional<FixOtpDefinition> fixOtpDefinition = findFixOtpDefinition(StringUtils.substring(secret, 0, 7));
        return fixOtpDefinition.map(otpDefinition -> otpDefinition.getOtp().equalsIgnoreCase(otp))
                .orElseGet(() -> timeBasedOTP.validateTOTP(otp, secret.getBytes()));
    }

    @Override
    public void saveUserPhoneCifs(String userId,
                                  String verificationToken,
                                  UserCifPhoneMapDTO userPhoneCifDTO) {

        String hashedToken = DigestUtils.sha256Hex(verificationToken);

        String key = formatOTPKey(userId, hashedToken);

        redisTemplate.opsForValue().set(key, userPhoneCifDTO, Duration.ofMinutes(5));
    }

    @Override
    public UserCifPhoneMapDTO getUserPhoneCifs(String userId, String verificationToken) {

        String hashedToken = DigestUtils.sha256Hex(verificationToken);

        String key = formatOTPKey(userId, hashedToken);

        return (UserCifPhoneMapDTO) getValue(key);
    }

    @Override
    public void saveReferralToCache(String userId, ReferralInfoDTO referral) {
        redisTemplate.opsForValue().set(userId, referral, Duration.ofMinutes(20));
    }

    @Override
    public void deleteUserFromCache(String userId) {
        userCache.remove(userId);
    }

    @Override
    public void saveRegistrationTypeToCache(String userId, String deviceId, String consumerId,
                                            KafkaEventType kafkaEventType) {
        if (Objects.isNull(deviceId)) {
            deviceId = "";
        }
        String key = DigestUtils.sha256Hex(userId + consumerId + deviceId);
        redisTemplate.opsForValue().set(key, kafkaEventType, Duration.ofMinutes(10));
    }

    private String formatOTPKey(String otp, String token) {
        return String.format(KEY_VALUE_FORMATTER, otp, token);
    }

    @Override
    public void saveToken(String userId, TokenType tokenType,
                          String token) {

        String hashedToken = Hashing.sha256()
                .hashString(token, StandardCharsets.UTF_8)
                .toString();

        String key = formatTokenKey(userId, hashedToken, tokenType);
        redisTemplate.opsForValue().set(key, hashedToken, 5, MINUTES);
    }

    @Override
    public void verifyToken(String userId, TokenType tokenType,
                            String token) {

        String hashedToken = Hashing.sha256()
                .hashString(token, StandardCharsets.UTF_8)
                .toString();

        String key = formatTokenKey(userId, hashedToken, tokenType);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            String cachedToken = (String) redisTemplate.opsForValue().get(key);
            if (!hashedToken.equals(cachedToken)) {
                throw new BadRequestException(BadRequestErrorEnum.INVALID_TOKEN);
            }
        } else {
            throw new BadRequestException(BadRequestErrorEnum.INVALID_TOKEN);
        }
    }

    @Override
    public void addUserToCache(User user) {
        redisTemplate.opsForValue().setIfAbsent(user.getId().toString(), user, 10, MINUTES);
    }

    private String formatTokenKey(String userId, String token, TokenType tokenType) {
        return String.format("%s_%s_%s", userId, token, tokenType.toString());
    }

}
