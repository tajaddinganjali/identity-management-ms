package com.management.limit.strategy.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.model.AuthorizationLimit;
import com.management.model.AuthorizationLimitConfig;
import com.management.model.EmbeddedLimit;
import com.management.repository.AuthorizationLimitConfigRepository;
import com.management.repository.AuthorizationLimitRepository;
import com.management.util.ExceptionUtil;
import com.management.util.TimeUtil;
import com.management.util.constant.ExecutorConstant;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("register_authorization")
@RequiredArgsConstructor
public class AuthorizationLimitWithSmsServiceStrategyImpl implements LimitServiceStrategy {

    private final AuthorizationLimitConfigRepository authorizationLimitConfigRepository;

    private final AuthorizationLimitRepository authorizationLimitRepository;


    @Override
    @Transactional
    public void checkLimit(String phone, LimitServiceTypeEnum serviceName) {
        Optional<AuthorizationLimit> optionalAuthorizationLimit =
                authorizationLimitRepository.findByPhoneAndService(phone, serviceName.getDbCode());
        checkAuthorizationLimit(optionalAuthorizationLimit);
    }

    @Override
    @Async(ExecutorConstant.RESET_WRONG_OTP_ATTEMPT)
    @Transactional
    public void resetScheduledWrongAttempt() {
        authorizationLimitRepository.resetWrongAuthorizationAttempt(
                LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS.getDbCode().longValue());
        authorizationLimitRepository.resetLockedAuthorizationAttempt(
                LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS.getDbCode().longValue());
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void doSuccessAttempt(String phone, LimitServiceTypeEnum serviceName) {
        AuthorizationLimitConfig authorizationLimitConfig =
                authorizationLimitConfigRepository.findByEmbeddedLimitConfigService(serviceName.getDbCode())
                        .orElseThrow(() -> new GeneralTechnicalException(
                                GeneralTechErrorEnum.SERVER_ERROR, "wrong_authorization_config"));
        Optional<AuthorizationLimit> authorizationLimitOptional =
                authorizationLimitRepository.findByPhoneAndService(phone, serviceName.getDbCode());
        checkAuthorizationLimit(authorizationLimitOptional);
        setAuthorizationLimit(phone, authorizationLimitConfig, authorizationLimitOptional);
    }

    private void setAuthorizationLimit(String phone, AuthorizationLimitConfig authorizationLimitConfig,
                                       Optional<AuthorizationLimit> optionalAuthorizationLimit) {
        AuthorizationLimit authorizationLimit;
        boolean hasLastOneSms = false;
        if (optionalAuthorizationLimit.isEmpty()) {
            authorizationLimit =
                    AuthorizationLimit.builder().authorizationLimitConfig(authorizationLimitConfig).phone(phone)
                            .attemptCount(1)
                            .embeddedLimit(EmbeddedLimit.builder().firstAttempt(TimeUtil.getCurrentDateTime())
                                    .lastAttempt(TimeUtil.getCurrentDateTime()).build()).build();
        } else {
            authorizationLimit = optionalAuthorizationLimit.get();
            authorizationLimit.setAttemptCount(authorizationLimit.getAttemptCount() + 1);
            authorizationLimit.setEmbeddedLimit(EmbeddedLimit.builder()
                    .firstAttempt(Objects.isNull(authorizationLimit.getEmbeddedLimit())
                            || Objects.isNull(authorizationLimit.getEmbeddedLimit().getFirstAttempt())
                            ? TimeUtil.getCurrentDateTime()
                            : authorizationLimit.getEmbeddedLimit().getFirstAttempt())
                    .lastAttempt(TimeUtil.getCurrentDateTime()).build());
            hasLastOneSms = authorizationLimitConfig.getEmbeddedLimitConfig().getAttemptCount()
                    - authorizationLimit.getAttemptCount() == 1;
        }
        authorizationLimitRepository.save(authorizationLimit);
        ExceptionUtil.throwAuthorizationSmsLimitException(hasLastOneSms);
    }

    public void checkAuthorizationLimit(Optional<AuthorizationLimit> optionalAuthorizationLimit) {
        if (optionalAuthorizationLimit.isPresent()) {
            AuthorizationLimit authorizationLimit = optionalAuthorizationLimit.get();
            checkAuthorizationBlock(authorizationLimit);
        }
    }

    private void checkAuthorizationBlock(AuthorizationLimit authorizationLimit) {
        boolean isExceededSmsAttempt = authorizationLimit.getAttemptCount()
                .compareTo(
                        authorizationLimit.getAuthorizationLimitConfig()
                                .getEmbeddedLimitConfig().getAttemptCount()) == 0;
        if (isExceededSmsAttempt) {
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_SEND_SMS,
                    LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS.toString());
        }
    }

}
