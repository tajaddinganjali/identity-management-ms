package com.management.register.service.impl;

import com.management.config.security.UserPrincipial;
import com.management.identity.dto.CheckUserRequisiteDTO;
import com.management.identity.service.IdentityService;
import com.management.identity.service.TokenService;
import com.management.identity.service.UserService;
import com.management.limit.LimitService;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.model.RegisterLimitAud;
import com.management.model.User;
import com.management.register.dto.RegisterLimitAudRequisiteDTO;
import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.dto.SetPasscodeRequestDTO;
import com.management.register.dto.SetPasscodeResponseDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.RegisterCaseTypeEnum;
import com.management.register.enums.ServiceName;
import com.management.register.service.RegisterLimitAuditService;
import com.management.register.service.RegisterService;
import com.management.register.service.RequestLogService;
import com.management.register.strategy.RegistrationStrategy;
import com.management.util.constant.PropertyConstants;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RegisterServiceImpl implements RegisterService {

    private final BeanFactory beanFactory;

    private final IdentityService identityService;

    private final LimitService limitService;

    private final RequestLogService requestLogService;

    private final RegisterLimitAuditService registerLimitAuditService;

    private final UserService userService;

    private final TokenService tokenService;

    @Override
    public RegisterPinResponseDTO registerWithPin(RegisterRequisiteDTO registerRequisite) {
        try {
            limitService.checkPermanentlyBlock(registerRequisite.getRegisterPinRequest().getPin());
            limitService.checkLimit(LimitServiceTypeEnum.getCheckLimitMap(registerRequisite));
            RegistrationStrategy registrationStrategy =
                    getRegistrationStrategy(registerRequisite.getHeaderInfo().getConsumer());
            RegisterCaseTypeEnum registerCaseType =
                    identityService.identifyUser(CheckUserRequisiteDTO.builder()
                            .pin(registerRequisite.getRegisterPinRequest().getPin())
                            .birthdate(registerRequisite.getRegisterPinRequest().getBirthDate())
                            .phone(registerRequisite.getRegisterPinRequest().getPhone())
                            .build());
            registerRequisite.setRegisterCaseType(registerCaseType);
            RegisterLimitAudRequisiteDTO registerLimitAudRequisite = RegisterLimitAudRequisiteDTO.builder()
                    .pin(registerRequisite.getRegisterPinRequest().getPin())
                    .phone(registerRequisite.getRegisterPinRequest().getPhone())
                    .build();

            checkWrongAttempt(registerRequisite, registerLimitAudRequisite);
            requestLogService.logRequestToDb(registerRequisite, ServiceName.REGISTER_BY_PIN, registerCaseType.name());

            return registrationStrategy.registerWithPin(registerRequisite);
        } catch (Exception exception) {
            requestLogService.logRequestToDb(registerRequisite, ServiceName.REGISTER_BY_PIN, exception.getMessage());
            throw exception;
        }
    }

    @Override
    public SetPasscodeResponseDTO setPasscode(UserPrincipial userPrincipial,
                                              SetPasscodeRequestDTO setPasscodeRequestDTO,
                                              String authorizationHeader, String lang)
            throws InvalidKeySpecException, NoSuchAlgorithmException {

        String userId = userPrincipial.getUserId();
        String consumerId = userPrincipial.getConsumerId();
        String deviceId = userPrincipial.getDeviceId();
        String passcode = setPasscodeRequestDTO.getPasscode();
        User user = userService.findActiveUserById(userId);
        String userName = user.getName();

        ThreadContext.put(PropertyConstants.USER_ID_PROPERTY_NAME, userId);
        String registrationToken = authorizationHeader.replace("Bearer ", "");
        tokenService.verifyRegistrationToken(userId, registrationToken);

        userService.setUserPassCode(userId, passcode, consumerId, lang, deviceId,
                setPasscodeRequestDTO.isAgreementSigned());

        return SetPasscodeResponseDTO.builder()
                .userId(userId)
                .name(userName)
                .build();
    }

    private void checkWrongAttempt(RegisterRequisiteDTO registerRequisite,
                                   RegisterLimitAudRequisiteDTO registerLimitAudRequisite) {
        boolean hasAttempt = limitService.hasAttempt(registerRequisite.getRegisterPinRequest().getPin(),
                registerRequisite.getRegisterPinRequest().getPhone());
        if (!hasAttempt) {
            try {
                limitService.doWrongAttempt(registerRequisite.getRegisterPinRequest().getPin(),
                        LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN);
                limitService.doWrongAttempt(registerRequisite.getRegisterPinRequest().getPhone(),
                        LimitServiceTypeEnum.REGISTER_WITH_SAME_PHONE);
            } catch (Exception exception) {
                doRegisterLimitAudit(registerLimitAudRequisite);
                throw exception;
            }
        }
        doRegisterLimitAudit(registerLimitAudRequisite);
    }

    private RegistrationStrategy getRegistrationStrategy(ConsumerTypeEnum consumerType) {
        return beanFactory.getBean(ConsumerTypeEnum.getRegisterStrategy(consumerType), RegistrationStrategy.class);
    }

    private void doRegisterLimitAudit(RegisterLimitAudRequisiteDTO registerLimitAudRequisite) {
        RegisterLimitAud registerLimitAud = RegisterLimitAud.builder()
                .pin(registerLimitAudRequisite.getPin())
                .phone(registerLimitAudRequisite.getPhone())
                .build();
        registerLimitAuditService.save(registerLimitAud);
    }

}
