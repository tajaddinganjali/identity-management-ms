package com.management.service;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.ErrorCodes;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.impl.LimitServiceImpl;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.limit.strategy.impl.OtpLimitServiceStrategyImpl;
import com.management.model.RegisterLimitAud;
import com.management.model.User;
import com.management.model.enums.UserStatus;
import com.management.repository.RegisterLimitAuditRepository;
import com.management.repository.UserRepository;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LimitServiceUnitTest {

    private static final String USER_PIN = "PIN";
    private static final String USER_PHONE = "PHONE";

    @Mock
    private Map<String, LimitServiceStrategy> limitServiceStrategies;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RegisterLimitAuditRepository registerLimitAuditRepository;
    @Mock
    private OtpLimitServiceStrategyImpl otpLimitServiceStrategy;
    @InjectMocks
    private LimitServiceImpl limitService;

    private static EnumMap<LimitServiceTypeEnum, String> getLimitServiceTypeEnumStringMap() {
        EnumMap<LimitServiceTypeEnum, String> checkMap = new EnumMap<>(LimitServiceTypeEnum.class);
        checkMap.put(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP, USER_PIN);
        checkMap.put(LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN, USER_PIN);
        return checkMap;
    }

    @Test
    void checkUserPermanentlyBlock() {
        User user = User.builder().id(UUID.randomUUID()).status(UserStatus.BLOCKED).build();
        Mockito.when(userRepository.findByPin(USER_PIN)).thenReturn(Optional.of(user));
        BadRequestException expectedException = Assertions.assertThrows(BadRequestException.class,
                () -> limitService.checkPermanentlyBlock(USER_PIN));
        Assertions.assertEquals(ErrorCodes.PERMANENTLY_BLOCKED, expectedException.getCode());
    }


    @Test
    void permanentlyBlockUser() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Mockito.when(userRepository.findByPin(USER_PIN)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);
        Assertions.assertDoesNotThrow(() -> limitService.permanentlyBlock(USER_PIN));
    }

    @Test
    void checkLimit() {
        Mockito.when(limitServiceStrategies.get(ArgumentMatchers.anyString()))
                .thenReturn(otpLimitServiceStrategy);
        Mockito.doNothing().when(otpLimitServiceStrategy)
                .checkLimit(ArgumentMatchers.anyString(), ArgumentMatchers.any(LimitServiceTypeEnum.class));
        Assertions.assertDoesNotThrow(
                () -> limitService
                        .checkLimit(getLimitServiceTypeEnumStringMap()));
    }

    @Test
    void doWrongAttempt() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Mockito.when(limitServiceStrategies.get(ArgumentMatchers.anyString()))
                .thenReturn(otpLimitServiceStrategy);
        Mockito.doThrow(new BadRequestException(BadRequestErrorEnum.PERMANENTLY_BLOCKED_OTP))
                .when(otpLimitServiceStrategy)
                .doWrongAttempt(ArgumentMatchers.anyString(), ArgumentMatchers.any(LimitServiceTypeEnum.class));
        Mockito.when(userRepository.findByPin(USER_PIN)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class,
                () -> limitService.doWrongAttempt(USER_PIN, LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP));
        Assertions.assertEquals(ErrorCodes.PERMANENTLY_BLOCKED_OTP, badRequestException.getCode());
        Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void resetAttempt() {
        Mockito.when(limitServiceStrategies.get(ArgumentMatchers.anyString()))
                .thenReturn(otpLimitServiceStrategy);
        Mockito.doNothing().when(otpLimitServiceStrategy).doSuccessAttempt(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(LimitServiceTypeEnum.class));
        Assertions.assertDoesNotThrow(
                () -> limitService.doSuccessAttempt(getLimitServiceTypeEnumStringMap()));
    }

    @Test
    void resetPerRoundWrongAttempt() {
        Mockito.when(limitServiceStrategies.get(ArgumentMatchers.anyString())).thenReturn(otpLimitServiceStrategy);
        Mockito.doNothing().when(otpLimitServiceStrategy).resetScheduledWrongAttempt();
        Assertions.assertDoesNotThrow(
                () -> limitService.resetScheduledWrongAttempt(
                        List.of(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP)));
    }

    @Test
    void hasAttempt_should_return_true() {
        List<RegisterLimitAud> registerLimitList =
                List.of(RegisterLimitAud.builder().pin(USER_PIN).phone(USER_PHONE).build());
        Mockito.when(registerLimitAuditRepository.findAllByPinAndPhoneAndCreatedDateAfter(
                Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(registerLimitList);
        Integer registerLimitAttemptTimeInHour = 24;
        ReflectionTestUtils.setField(limitService, "registerLimitAttemptTimeInHour", registerLimitAttemptTimeInHour);
        Assertions.assertDoesNotThrow(() -> limitService.hasAttempt(USER_PIN, USER_PHONE));
        boolean actual = limitService.hasAttempt(USER_PIN, USER_PHONE);
        Assertions.assertTrue(actual);
    }

    @Test
    void hasAttempt_should_return_false() {
        List<RegisterLimitAud> registerLimitList = new ArrayList<>();
        Mockito.when(registerLimitAuditRepository.findAllByPinAndPhoneAndCreatedDateAfter(
                Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(registerLimitList);
        Integer registerLimitAttemptTimeInHour = 24;
        ReflectionTestUtils.setField(limitService, "registerLimitAttemptTimeInHour", registerLimitAttemptTimeInHour);
        Assertions.assertDoesNotThrow(() -> limitService.hasAttempt(USER_PIN, USER_PHONE));
        boolean actual = limitService.hasAttempt(USER_PIN, USER_PHONE);
        Assertions.assertFalse(actual);
    }

}
