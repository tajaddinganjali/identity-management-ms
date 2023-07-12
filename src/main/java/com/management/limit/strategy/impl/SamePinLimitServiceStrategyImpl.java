package com.management.limit.strategy.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.model.EmbeddedLimit;
import com.management.model.RegisterLimit;
import com.management.model.RegisterLimitConfig;
import com.management.repository.RegisterLimitConfigRepository;
import com.management.repository.RegisterLimitRepository;
import com.management.util.ExceptionUtil;
import com.management.util.TimeUtil;
import com.management.util.constant.ExecutorConstant;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("register_with_same_pin")
@RequiredArgsConstructor
public class SamePinLimitServiceStrategyImpl implements LimitServiceStrategy {

    private final RegisterLimitRepository registerLimitRepository;

    private final RegisterLimitConfigRepository registerLimitConfigRepository;


    @Override
    @Transactional
    public void checkLimit(String pin, LimitServiceTypeEnum serviceName) {
        Optional<RegisterLimit> optionalRegisterLimit =
                registerLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        checkRegisterLimit(optionalRegisterLimit);
    }


    @Override
    @Async(ExecutorConstant.RESET_WRONG_OTP_ATTEMPT)
    @Transactional
    public void resetScheduledWrongAttempt() {
        registerLimitRepository.resetWrongRegisterAttempts(
                LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN.getDbCode().longValue());
        registerLimitRepository.resetWrongRegisterAttemptsLock(
                LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN.getDbCode().longValue());
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void doWrongAttempt(String pin, LimitServiceTypeEnum serviceName) {
        RegisterLimitConfig registerLimitConfig =
                registerLimitConfigRepository.findByEmbeddedLimitConfigService(serviceName.getDbCode())
                        .orElseThrow(() -> new GeneralTechnicalException(
                                GeneralTechErrorEnum.SERVER_ERROR, "wrong_register_config"));
        Optional<RegisterLimit> optionalRegisterLimit =
                registerLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        checkRegisterLimit(optionalRegisterLimit);
        setRegisterLimit(pin, registerLimitConfig, optionalRegisterLimit);
    }

    @Override
    public void doSuccessAttempt(String pin, LimitServiceTypeEnum serviceName) {
        Optional<RegisterLimit> optionalRegisterLimit =
                registerLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        if (optionalRegisterLimit.isPresent()) {
            RegisterLimit registerLimit = optionalRegisterLimit.get();
            registerLimit.setAttemptCount(0);
            registerLimit.getEmbeddedLimit().setLastAttempt(TimeUtil.getCurrentDateTime());
            registerLimit.getEmbeddedLimit().setFirstAttempt(TimeUtil.getCurrentDateTime());
            registerLimitRepository.save(registerLimit);
        }
    }

    @Override
    public boolean hasWrongAttempt(String pin) {
        Optional<RegisterLimit> optionalRegisterLimit = registerLimitRepository
                .findByPinAndService(pin, LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN.getDbCode());
        return optionalRegisterLimit.isPresent() && optionalRegisterLimit.get().getAttemptCount() > 0;
    }

    private void setRegisterLimit(String pin, RegisterLimitConfig registerLimitConfig,
                                  Optional<RegisterLimit> optionalRegisterLimit) {
        RegisterLimit registerLimit;
        boolean isLastWrongAttempt = false;
        if (optionalRegisterLimit.isEmpty()) {
            registerLimit = RegisterLimit.builder()
                    .registerLimitConfig(registerLimitConfig)
                    .embeddedLimit(EmbeddedLimit.builder().pin(pin).firstAttempt(TimeUtil.getCurrentDateTime()).build())
                    .attemptCount(1).build();
        } else {
            registerLimit = optionalRegisterLimit.get();
            isLastWrongAttempt =
                    registerLimitConfig.getEmbeddedLimitConfig().getAttemptCount()
                            .compareTo(registerLimit.getAttemptCount() + 1) == 0;
            registerLimit.setAttemptCount(registerLimit.getAttemptCount() + 1);
            registerLimit.setEmbeddedLimit(EmbeddedLimit.builder()
                    .firstAttempt(Objects.isNull(registerLimit.getEmbeddedLimit())
                            || Objects.isNull(registerLimit.getEmbeddedLimit().getFirstAttempt())
                            ? TimeUtil.getCurrentDateTime()
                            : registerLimit.getEmbeddedLimit().getFirstAttempt())
                    .pin(pin).build());
        }
        registerLimit.getEmbeddedLimit().setLastAttempt(TimeUtil.getCurrentDateTime());
        registerLimitRepository.save(registerLimit);
        ExceptionUtil.throwRegisterSamePinLimitException(isLastWrongAttempt);
    }


    public void checkRegisterLimit(Optional<RegisterLimit> optionalRegisterLimit) {
        if (optionalRegisterLimit.isPresent()) {
            RegisterLimit registerLimit = optionalRegisterLimit.get();
            checkRegisterBlock(registerLimit);
        }
    }

    private void checkRegisterBlock(RegisterLimit registerLimit) {
        boolean isLastWrongRegisterAttempt = registerLimit.getAttemptCount()
                .compareTo(registerLimit.getRegisterLimitConfig().getEmbeddedLimitConfig().getAttemptCount()) == 0;
        if (isLastWrongRegisterAttempt) {
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_SAME_PIN,
                    LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN.toString());
        }
    }

}
