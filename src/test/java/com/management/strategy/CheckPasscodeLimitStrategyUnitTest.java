package com.management.strategy;

import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.impl.PasscodeLimitServiceStrategyImpl;
import com.management.model.User;
import com.management.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class CheckPasscodeLimitStrategyUnitTest {

    private static final String UUID = "17954edc-efd6-11ec-8ea0-0242ac120002";
    @Mock
    RMapCache<Object, Object> userLockCache;
    @Mock
    RMapCache<Object, Object> userLockDurationCache;
    @Mock
    RMapCache<Object, Object> passcodeLimitCache;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RedissonClient redissonClient;
    private PasscodeLimitServiceStrategyImpl checkPasscodeLimitServiceStrategy;


    @BeforeEach
    void init() {
        Mockito.when(redissonClient.getMapCache("userblock")).thenReturn(userLockCache);
        Mockito.when(redissonClient.getMapCache("userblockduration")).thenReturn(userLockDurationCache);
        Mockito.when(redissonClient.getMapCache("passcodelimit")).thenReturn(passcodeLimitCache);
        checkPasscodeLimitServiceStrategy = new PasscodeLimitServiceStrategyImpl(redissonClient, userRepository);
    }

    @Test
    void checkLimit() {
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> checkPasscodeLimitServiceStrategy
                        .checkLimit("pin", LimitServiceTypeEnum.LOGIN_CHECK_PASSCODE));
    }

    @Test
    void doSuccessAttempt_throws() {
        Mockito.when(userRepository.findByPin(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(() -> checkPasscodeLimitServiceStrategy.doSuccessAttempt("pin",
                LimitServiceTypeEnum.LOGIN_CHECK_PASSCODE));
    }

    @Test
    void doSuccessAttempt() {
        Mockito.when(userRepository.findByPin(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(User.builder().id(java.util.UUID.fromString(UUID)).build()));
        Assertions.assertDoesNotThrow(() -> checkPasscodeLimitServiceStrategy.doSuccessAttempt("pin",
                LimitServiceTypeEnum.LOGIN_CHECK_PASSCODE));
    }

    @Test
    void doWrongAttempt_throws() {
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> checkPasscodeLimitServiceStrategy.doWrongAttempt("PIN",
                        LimitServiceTypeEnum.LOGIN_CHECK_PASSCODE));
    }

    @Test
    void resetScheduledWrongAttempt_throws() {
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> checkPasscodeLimitServiceStrategy.resetScheduledWrongAttempt());
    }

}

