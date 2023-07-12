package com.management.strategy;

import com.management.exception.BadRequestException;
import com.management.exception.ErrorCodes;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.impl.SamePhoneLimitServiceStrategyImpl;
import com.management.model.EmbeddedLimit;
import com.management.model.EmbeddedLimitConfig;
import com.management.model.RegisterLimit;
import com.management.model.RegisterLimitConfig;
import com.management.repository.RegisterLimitConfigRepository;
import com.management.repository.RegisterLimitRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SamePhoneLimitServiceStrategyImplTest {

    private static final String USER_PHONE = "PHONE";
    private static final LimitServiceTypeEnum LIMIT_SERVICE_TYPE = LimitServiceTypeEnum.REGISTER_WITH_SAME_PHONE;

    private static EmbeddedLimit embeddedLimit = EmbeddedLimit.builder()
            .pin("PIN")
            .build();

    @Mock
    private RegisterLimitRepository registerLimitRepository;
    @Mock
    private RegisterLimitConfigRepository registerLimitConfigRepository;
    @InjectMocks
    private SamePhoneLimitServiceStrategyImpl checkSamePhoneLimitServiceStrategy;

    static Stream<RegisterLimit> doWrongOtpAttemptSource() {
        return Stream.of(null, RegisterLimit
                .builder()
                .embeddedLimit(embeddedLimit)
                .attemptCount(2)
                .registerLimitConfig(RegisterLimitConfig.builder()
                        .embeddedLimitConfig(EmbeddedLimitConfig.builder()
                                .attemptCount(3)
                                .build())
                        .build())
                .build());
    }

    @Test
    void checkLimit_throws_permanent_block() {
        RegisterLimit registerLimit = RegisterLimit.builder().attemptCount(3).registerLimitConfig(
                RegisterLimitConfig.builder().embeddedLimitConfig(EmbeddedLimitConfig.builder()
                        .attemptCount(3).build()).build()).build();
        Mockito.when(registerLimitRepository.findByPhoneAndService(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.of(registerLimit));
        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> checkSamePhoneLimitServiceStrategy.checkLimit(USER_PHONE, LIMIT_SERVICE_TYPE));
        Assertions.assertEquals(ErrorCodes.TEMPORARY_BLOCKED_SAME_PHONE, actualException.getCode());
    }

    @Test
    void resetScheduledWrongAttempt() {
        Mockito.doNothing().when(registerLimitRepository).resetWrongRegisterAttemptsLock(ArgumentMatchers.anyLong());
        Assertions.assertDoesNotThrow(() -> checkSamePhoneLimitServiceStrategy.resetScheduledWrongAttempt());
    }


    @Test
    void doSuccessAttempt() {
        Mockito.when(registerLimitRepository.findByPhoneAndService(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.of(RegisterLimit.builder()
                .embeddedLimit(embeddedLimit)
                .id(1L).attemptCount(2).build()));
        Mockito.when(registerLimitRepository.save(ArgumentMatchers.any(RegisterLimit.class)))
                .thenReturn(RegisterLimit.builder().id(1L).build());
        Assertions.assertDoesNotThrow(
                () -> checkSamePhoneLimitServiceStrategy.doSuccessAttempt(USER_PHONE, LIMIT_SERVICE_TYPE));
    }

    @Test
    void doWrongAttempt_wrong_limit_config() {
        Mockito.when(
                        registerLimitConfigRepository.findByEmbeddedLimitConfigService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> checkSamePhoneLimitServiceStrategy.doWrongAttempt(USER_PHONE,
                        LIMIT_SERVICE_TYPE));
    }

    @ParameterizedTest
    @MethodSource("doWrongOtpAttemptSource")
    void doWrongAttempt(RegisterLimit otpLimit) {
        Mockito.when(registerLimitConfigRepository
                        .findByEmbeddedLimitConfigService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(RegisterLimitConfig.builder()
                        .embeddedLimitConfig(EmbeddedLimitConfig.builder().attemptCount(2).build())
                        .id(1L)
                        .build()));
        Mockito.when(registerLimitRepository.findByPhoneAndService(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.ofNullable(otpLimit));
        Mockito.when(registerLimitRepository.save(ArgumentMatchers.any(RegisterLimit.class)))
                .thenReturn(RegisterLimit.builder()
                        .embeddedLimit(embeddedLimit)
                        .id(1L).build());
        Assertions.assertDoesNotThrow(
                () -> checkSamePhoneLimitServiceStrategy.doWrongAttempt(USER_PHONE, LIMIT_SERVICE_TYPE));
    }


    @Test
    void hasWrongAttempt() {
        Mockito.when(registerLimitRepository.findByPhoneAndService(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.of(RegisterLimit.builder()
                .embeddedLimit(embeddedLimit)
                .id(1L).attemptCount(2).build()));
        boolean actual = checkSamePhoneLimitServiceStrategy.hasWrongAttempt(USER_PHONE);
        Assertions.assertEquals(Boolean.TRUE, actual);
    }

}
