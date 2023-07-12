package com.management.service;

import com.management.config.security.UserPrincipial;
import com.management.identity.service.IdentityService;
import com.management.identity.service.TokenService;
import com.management.identity.service.UserService;
import com.management.limit.LimitService;
import com.management.model.User;
import com.management.register.dto.RegisterPinRequestDTO;
import com.management.register.dto.SetPasscodeRequestDTO;
import com.management.register.dto.internal.HeaderInfoDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.RegisterCaseTypeEnum;
import com.management.register.service.RegisterLimitAuditService;
import com.management.register.service.RequestLogService;
import com.management.register.service.impl.RegisterServiceImpl;
import com.management.register.strategy.RegistrationStrategy;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.BeanFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterServiceUnitTest {

    @Mock
    private BeanFactory beanFactory;
    @Mock
    private IdentityService identityService;
    @Mock
    private LimitService limitService;
    @Mock
    private RegistrationStrategy registrationStrategy;
    @Mock
    private UserService userService;
    @Mock
    private TokenService tokenService;
    @Mock
    private RequestLogService requestLogService;
    @Mock
    private RegisterLimitAuditService registerLimitAuditService;
    @InjectMocks
    private RegisterServiceImpl registerService;

    @Test
    void registerWithPin() {
        final RegisterRequisiteDTO registerRequisiteDTO = RegisterRequisiteDTO.builder()
                .registerPinRequest(RegisterPinRequestDTO.builder()
                        .pin("AAA1111")
                        .birthDate("10/10/2000")
                        .phone("994555555555")
                        .build())
                .headerInfo(HeaderInfoDTO.builder().consumer(ConsumerTypeEnum.MOBILE).build())
                .build();
        Mockito.when(beanFactory.getBean(ConsumerTypeEnum.getRegisterStrategy(ConsumerTypeEnum.MOBILE),
                RegistrationStrategy.class)).thenReturn(registrationStrategy);
        Mockito.when(limitService.hasAttempt(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(Boolean.TRUE);
        Mockito.when(identityService.identifyUser(ArgumentMatchers.any())).thenReturn(RegisterCaseTypeEnum.REGISTER_PIN_SUCCESS);
        Assertions.assertDoesNotThrow(() -> registerService.registerWithPin(registerRequisiteDTO));
    }

    @Test
    void registerWithPin_checkWrongAttempt() {
        final RegisterRequisiteDTO registerRequisiteDTO = RegisterRequisiteDTO.builder()
                .registerPinRequest(RegisterPinRequestDTO.builder().pin("AAA1111").build()).build();
        Assertions.assertThrows(Exception.class,
                () -> registerService.registerWithPin(registerRequisiteDTO));
    }

    @Test
    void setPassCode() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserPrincipial userPrincipial = UserPrincipial.builder()
                .userId("userId")
                .consumerId("consumerId")
                .deviceId("deviceId")
                .build();
        SetPasscodeRequestDTO setPasscodeRequestDTO = new SetPasscodeRequestDTO("passcode", false);
        Mockito.when(userService.findActiveUserById(Mockito.any())).thenReturn(User.builder().name("name").build());
        Mockito.doNothing().when(tokenService).verifyRegistrationToken(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(userService).setUserPassCode(Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Assertions.assertDoesNotThrow(() -> registerService.setPasscode(
                userPrincipial, setPasscodeRequestDTO, "header", "lang"));
    }

}
