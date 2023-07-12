package com.management.strategy;

import com.management.exception.BadRequestException;
import com.management.exception.ErrorCodes;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.impl.AuthorizationLimitWithSmsServiceStrategyImpl;
import com.management.model.AuthorizationLimit;
import com.management.model.AuthorizationLimitConfig;
import com.management.model.EmbeddedLimit;
import com.management.model.EmbeddedLimitConfig;
import com.management.repository.AuthorizationLimitConfigRepository;
import com.management.repository.AuthorizationLimitRepository;
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
class AuthorizationLimitWithSmsServiceStrategyImplTest {

    private static final String USER_PHONE = "PHONE";
    private static final LimitServiceTypeEnum LIMIT_SERVICE_TYPE = LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS;
    private static EmbeddedLimit embeddedLimit = EmbeddedLimit.builder()
            .pin("PIN")
            .build();

    private static EmbeddedLimitConfig embeddedLimitConfig = EmbeddedLimitConfig.builder()
            .attemptCount(3)
            .build();

    @Mock
    private AuthorizationLimitConfigRepository authorizationLimitConfigRepository;

    @Mock
    private AuthorizationLimitRepository authorizationLimitRepository;

    @InjectMocks
    private AuthorizationLimitWithSmsServiceStrategyImpl checkSmsLimitServiceStrategy;

    static Stream<AuthorizationLimit> doSuccessOtpAttemptSource() {
        return Stream.of(null, AuthorizationLimit
                .builder()
                .embeddedLimit(embeddedLimit)
                .attemptCount(2)
                .authorizationLimitConfig(AuthorizationLimitConfig.builder()
                        .embeddedLimitConfig(embeddedLimitConfig)
                        .build())
                .build());
    }

    @Test
    void checkLimit_throws_permanent_block() {
        AuthorizationLimit authorizationLimit = AuthorizationLimit.builder().attemptCount(3).authorizationLimitConfig(
                AuthorizationLimitConfig.builder().embeddedLimitConfig(EmbeddedLimitConfig.builder()
                        .attemptCount(3).build()).build()).build();
        Mockito.when(authorizationLimitRepository
                        .findByPhoneAndService(ArgumentMatchers.anyString(),
                                ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(authorizationLimit));
        BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                () -> checkSmsLimitServiceStrategy.checkLimit(USER_PHONE, LIMIT_SERVICE_TYPE));
        Assertions.assertEquals(ErrorCodes.TEMPORARY_BLOCKED_SEND_SMS, actualException.getCode());
    }

    @Test
    void resetScheduledWrongAttempt() {
        Mockito.doNothing().when(authorizationLimitRepository).resetLockedAuthorizationAttempt(ArgumentMatchers.anyLong());
        Assertions.assertDoesNotThrow(() -> checkSmsLimitServiceStrategy.resetScheduledWrongAttempt());
    }


    @Test
    void doSuccessAttempt_wrong_limit_config() {
        Mockito.when(authorizationLimitConfigRepository.findByEmbeddedLimitConfigService(
                        ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> checkSmsLimitServiceStrategy.doSuccessAttempt(USER_PHONE, LIMIT_SERVICE_TYPE));
    }

    @ParameterizedTest
    @MethodSource("doSuccessOtpAttemptSource")
    void doSuccessAttempt(AuthorizationLimit authorizationLimit) {
        Mockito.when(authorizationLimitConfigRepository
                        .findByEmbeddedLimitConfigService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(AuthorizationLimitConfig.builder()
                        .embeddedLimitConfig(EmbeddedLimitConfig.builder()
                                .attemptCount(2)
                                .build())
                        .id(1L)

                        .build()));
        Mockito.when(
                        authorizationLimitRepository.findByPhoneAndService(ArgumentMatchers.anyString(),
                                ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.ofNullable(authorizationLimit));
        Mockito.when(authorizationLimitRepository.save(ArgumentMatchers.any(AuthorizationLimit.class)))
                .thenReturn(AuthorizationLimit.builder()
                        .embeddedLimit(embeddedLimit)
                        .id(1L).build());
        Assertions.assertDoesNotThrow(
                () -> checkSmsLimitServiceStrategy.doSuccessAttempt(USER_PHONE, LIMIT_SERVICE_TYPE));
    }

}
