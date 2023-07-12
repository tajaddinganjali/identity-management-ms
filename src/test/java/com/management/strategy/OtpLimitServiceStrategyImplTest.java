package com.management.strategy;

import com.management.exception.BadRequestException;
import com.management.exception.ErrorCodes;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.impl.OtpLimitServiceStrategyImpl;
import com.management.model.EmbeddedLimit;
import com.management.model.OtpLimit;
import com.management.model.OtpLimitConfig;
import com.management.repository.OtpLimitConfigRepository;
import com.management.repository.OtpLimitRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
class OtpLimitServiceStrategyImplTest {

    private static final String USER_PIN = "PIN";
    private static final LimitServiceTypeEnum LIMIT_SERVICE_TYPE = LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP;
    private static EmbeddedLimit embeddedLimit = EmbeddedLimit.builder()
            .pin("PIN")
            .build();

    @Mock
    private OtpLimitRepository otpLimitRepository;
    @Mock
    private OtpLimitConfigRepository otpLimitConfigRepository;
    @InjectMocks
    private OtpLimitServiceStrategyImpl otpLimitServiceStrategy;

    static Stream<Arguments> checkLimitThrowsSource() {

        return Stream.of(Arguments.of(OtpLimit
                        .builder()
                        .failRound(3)
                        .perRoundAttempt(3)
                        .otpLimitConfig(OtpLimitConfig.builder()
                                .failRound(3)
                                .perRoundAttempt(3)
                                .build())
                        .build(), ErrorCodes.PERMANENTLY_BLOCKED_OTP),
                Arguments.of(OtpLimit
                        .builder()
                        .failRound(2)
                        .perRoundAttempt(3)
                        .otpLimitConfig(OtpLimitConfig.builder()
                                .failRound(3)
                                .perRoundAttempt(3)
                                .build())
                        .build(), ErrorCodes.TEMPORARY_BLOCKED_OTP));
    }

    static Stream<OtpLimit> doWrongOtpAttemptSource() {
        return Stream.of(null, OtpLimit
                .builder()
                .embeddedLimit(embeddedLimit)
                .failRound(3)
                .perRoundAttempt(1)
                .otpLimitConfig(OtpLimitConfig.builder()
                        .failRound(3)
                        .perRoundAttempt(3)
                        .build())
                .build());


    }

    static Stream<Arguments> doWrongOtpAttemptSourceThrows() {
        return Stream.of(Arguments.of(1, 2, ErrorCodes.TEMPORARY_BLOCKED_OTP_IN_MINUTES),
                Arguments.of(3, 2, ErrorCodes.PERMANENTLY_BLOCKED_OTP));
    }


    @ParameterizedTest
    @MethodSource("checkLimitThrowsSource")
    void checkLimit_throws_permanent_block_wrong_otp(OtpLimit otpLimit, String errorCode) {
        Mockito.when(otpLimitRepository.findByPinAndService(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.of(otpLimit));
        BadRequestException expectedException = Assertions.assertThrows(BadRequestException.class,
                () -> otpLimitServiceStrategy.checkLimit(USER_PIN, LIMIT_SERVICE_TYPE));
        Assertions.assertEquals(errorCode, expectedException.getCode());
    }

    @Test
    void resetOtpAttempt() {
        Mockito.doNothing().when(otpLimitRepository).resetPerRoundWrongAttemptOtp(ArgumentMatchers.anyLong());
        Assertions.assertDoesNotThrow(() -> otpLimitServiceStrategy.resetScheduledWrongAttempt());
    }

    @Test
    void resetAttempt() {
        Mockito.when(otpLimitRepository.findByPinAndService(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.of(OtpLimit.builder()
                .id(1L)
                .perRoundAttempt(1)
                .embeddedLimit(embeddedLimit)
                .failRound(2)
                .build()));
        Mockito.when(otpLimitRepository.save(ArgumentMatchers.any(OtpLimit.class)))
                .thenReturn(OtpLimit.builder().id(1L).build());
        Assertions.assertDoesNotThrow(() -> otpLimitServiceStrategy.doSuccessAttempt(USER_PIN, LIMIT_SERVICE_TYPE));
    }

    @Test
    void doWrongAttempt_wrong_limit_config() {
        Mockito.when(otpLimitConfigRepository.findByService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(GeneralTechnicalException.class, () -> otpLimitServiceStrategy.doWrongAttempt(USER_PIN,
                LIMIT_SERVICE_TYPE));
    }

    @ParameterizedTest
    @MethodSource("doWrongOtpAttemptSource")
    void doWrongAttempt(OtpLimit otpLimit) {
        Mockito.when(otpLimitConfigRepository
                        .findByService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(OtpLimitConfig.builder()
                        .perRoundAttempt(3)
                        .failRound(3)
                        .roundLockTime("15#30#60")
                        .id(1L)
                        .build()));
        Mockito.when(otpLimitRepository.findByPinAndService(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.ofNullable(otpLimit));
        Mockito.when(otpLimitRepository.save(ArgumentMatchers.any(OtpLimit.class)))
                .thenReturn(OtpLimit.builder().id(1L).build());
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class,
                () -> otpLimitServiceStrategy.doWrongAttempt(USER_PIN,
                        LIMIT_SERVICE_TYPE));
        Assertions.assertEquals(ErrorCodes.INVALID_OTP_ATTEMPT, badRequestException.getCode());
    }


    @MethodSource("doWrongOtpAttemptSourceThrows")
    @ParameterizedTest
    @MockitoSettings(strictness = Strictness.LENIENT)
    void doWrongOtpAttemptSource_throws(Integer failRound, Integer perRound, String errorCode) {
        OtpLimit otpLimit = OtpLimit.builder()
                .embeddedLimit(EmbeddedLimit.builder()
                        .pin("PIN")
                        .build())
                .failRound(failRound).perRoundAttempt(perRound).otpLimitConfig(OtpLimitConfig
                        .builder().failRound(3).perRoundAttempt(3).build()).build();
        Mockito.when(otpLimitConfigRepository
                        .findByService(ArgumentMatchers.any(Integer.class)))
                .thenReturn(Optional.of(OtpLimitConfig.builder().perRoundAttempt(3)
                        .failRound(3).roundLockTime("15#30#60").id(1L).build()));
        Mockito.when(otpLimitRepository.findByPinAndService(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(Integer.class))).thenReturn(Optional.ofNullable(otpLimit));
        Mockito.when(otpLimitRepository.save(ArgumentMatchers.any(OtpLimit.class)))
                .thenReturn(OtpLimit.builder().id(1L).build());
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class,
                () -> otpLimitServiceStrategy.doWrongAttempt(USER_PIN, LIMIT_SERVICE_TYPE));
        Assertions.assertEquals(errorCode, badRequestException.getCode());
    }

    @Test
    void hasWrongAttempt() {
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> otpLimitServiceStrategy.hasWrongAttempt(USER_PIN));
    }

}
