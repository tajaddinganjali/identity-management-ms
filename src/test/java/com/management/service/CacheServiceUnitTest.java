package com.management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.management.exception.BadRequestException;
import com.management.model.FixOtpDefinition;
import com.management.model.OtpDefinition;
import com.management.model.User;
import com.management.otp.dto.UserCifPhoneMapDTO;
import com.management.otp.service.impl.CacheServiceImpl;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.enums.TokenType;
import com.management.repository.FixOtpDefinitionRepository;
import com.management.repository.OtpDefinitionRepository;
import com.management.util.TimeBasedOTP;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CacheServiceUnitTest {

    @Mock
    private FixOtpDefinitionRepository fixOtpDefinitionRepository;
    @Mock
    private OtpDefinitionRepository otpDefinitionRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RMapCache<Object, Object> userCache;
    @Mock
    private TypedJsonJacksonCodec typedJsonJacksonCodec;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private TimeBasedOTP timeBasedOTP;
    @InjectMocks
    private CacheServiceImpl cacheService;

    @BeforeEach
    void init() {
        Mockito.when(redissonClient.getMapCache(anyString(), any(TypedJsonJacksonCodec.class))).thenReturn(userCache);
        cacheService = new CacheServiceImpl(fixOtpDefinitionRepository, typedJsonJacksonCodec, otpDefinitionRepository,
                redissonClient, redisTemplate, timeBasedOTP);
    }

    @Test
    void findFixOtpDefinition() {
        Mockito.when(fixOtpDefinitionRepository.findFirstByPin(Mockito.anyString()))
                .thenReturn(Optional.of(FixOtpDefinition.builder().build()));
        Assertions.assertDoesNotThrow(() -> cacheService.findFixOtpDefinition("pin"));
    }

    @Test
    void findOtpDefinition() {
        Mockito.when(otpDefinitionRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(OtpDefinition.builder().build()));
        Assertions.assertDoesNotThrow(() -> cacheService.findOtpDefinition(UUID.randomUUID().toString()));
    }

    @Test
    void hasKey() {
        Mockito.when(redisTemplate.hasKey(Mockito.anyString())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.hasKey("token"));
    }

    @Test
    void delete() {
        Mockito.when(redisTemplate.delete(Mockito.anyString())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.delete("token"));
    }

    @Test
    void getValue() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get(any())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.getValue("token"));
    }

    @Test
    void setValue() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Assertions.assertDoesNotThrow(() -> cacheService.setValue("token", RegisterCacheDTO.builder().build(), 1));
    }

    @Test
    void createTOTP() {
        Mockito.when(timeBasedOTP.generateTOTP(Mockito.anyString())).thenReturn("generated");
        Assertions.assertDoesNotThrow(() -> cacheService.createTOTP("key"));
    }

    @Test
    void validateTOTP() {
        Mockito.when(timeBasedOTP.validateTOTP(Mockito.anyString(), any())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.validateTOTP("otp", "secret"));
    }

    @Test
    void saveUserPhoneCifs() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Assertions.assertDoesNotThrow(() -> cacheService.saveUserPhoneCifs(
                "userId", "token", UserCifPhoneMapDTO.builder().build()));
    }

    @Test
    void getUserPhoneCifs() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get(any())).thenReturn(UserCifPhoneMapDTO.builder().build());
        Assertions.assertDoesNotThrow(() -> cacheService.getUserPhoneCifs("userId", "token"));
    }

    @Test
    void saveToken() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.doNothing().when(valueOperations)
                .set("userId_3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0_AUTHORIZATION_TOKEN",
                        "3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0", 5L, TimeUnit.MINUTES);
        Assertions.assertDoesNotThrow(() -> cacheService.saveToken("userId", TokenType.AUTHORIZATION_TOKEN, "token"));
    }

    @Test
    void verifyToken() {
        final String key = "userId_3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0_"
                + "AUTHORIZATION_TOKEN_db37d21181592000efad06f87a00afba59f9c99b11e119d118be2b929c3387"
                + "ce_AUTHORIZATION_TOKEN";
        Mockito.when(redisTemplate.hasKey(key)).thenReturn(Boolean.TRUE);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get(key))
                .thenReturn("db37d21181592000efad06f87a00afba59f9c99b11e119d118be2b929c3387ce");
        Assertions.assertDoesNotThrow(() -> cacheService.verifyToken(
                "userId_3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0_AUTHORIZATION_TOKEN",
                TokenType.AUTHORIZATION_TOKEN, "3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void verifyToken_exception(Boolean value) {
        final String key = "userId_3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0_"
                + "AUTHORIZATION_TOKEN_db37d21181592000efad06f87a00afba59f9c99b11e119d118be2b929c3387"
                + "ce_AUTHORIZATION_TOKEN";
        Mockito.when(redisTemplate.hasKey(key)).thenReturn(value);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get(key)).thenReturn("token");
        Assertions.assertThrows(BadRequestException.class, () -> cacheService.verifyToken(
                "userId_3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0_AUTHORIZATION_TOKEN",
                TokenType.AUTHORIZATION_TOKEN, "3c469e9d6c5875d37a43f353d4f88e61fcf812c66eee3457465a40b0da4153e0"));
    }

    @Test
    void addUserToCache() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.setIfAbsent(user.getId().toString(), user, 10L, TimeUnit.MINUTES))
                .thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.addUserToCache(user));
    }

    @Test
    void deleteUserFromCache() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Mockito.when(userCache.remove(user.getId())).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> cacheService.deleteUserFromCache(user.getId().toString()));
    }

}
