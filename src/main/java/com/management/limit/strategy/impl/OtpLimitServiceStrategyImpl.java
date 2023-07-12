package com.management.limit.strategy.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.model.EmbeddedLimit;
import com.management.model.OtpLimit;
import com.management.model.OtpLimitConfig;
import com.management.repository.OtpLimitConfigRepository;
import com.management.repository.OtpLimitRepository;
import com.management.util.ExceptionUtil;
import com.management.util.TimeUtil;
import com.management.util.constant.ExecutorConstant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("register_with_pin_otp")
@RequiredArgsConstructor
public class OtpLimitServiceStrategyImpl implements LimitServiceStrategy {

    private final OtpLimitRepository otpLimitRepository;

    private final OtpLimitConfigRepository otpLimitConfigRepository;


    @Override
    @Transactional
    public void checkLimit(String pin, LimitServiceTypeEnum serviceName) {
        Optional<OtpLimit> optionalOtpLimit =
                otpLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        checkOtpLimit(optionalOtpLimit);
    }


    @Override
    @Async(ExecutorConstant.RESET_WRONG_OTP_ATTEMPT)
    @Transactional
    public void resetScheduledWrongAttempt() {
        otpLimitRepository.resetPerRoundWrongAttemptOtp(
                LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP.getDbCode().longValue());
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void doWrongAttempt(String pin, LimitServiceTypeEnum serviceName) {
        OtpLimitConfig otpLimitConfig =
                otpLimitConfigRepository.findByService(serviceName.getDbCode()).orElseThrow(() -> new GeneralTechnicalException(
                        GeneralTechErrorEnum.SERVER_ERROR, "wrong_limit_config"));
        Optional<OtpLimit> optionalOtpLimit =
                otpLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        checkOtpLimit(optionalOtpLimit);
        setOtpLimit(pin, otpLimitConfig, optionalOtpLimit);
    }

    private void setOtpLimit(String pin, OtpLimitConfig otpLimitConfig, Optional<OtpLimit> optionalOtpLimit) {
        OtpLimit otpLimit;
        boolean isLastPerRoundWrongAttempt = false;
        boolean isLastWrongRoundAttempt = false;
        int lockTimeInMinute = 0;
        if (optionalOtpLimit.isEmpty()) {
            otpLimit = OtpLimit.builder().otpLimitConfig(otpLimitConfig).perRoundAttempt(1)
                    .failRound(0).embeddedLimit(EmbeddedLimit.builder()
                            .firstAttempt(TimeUtil.getCurrentDateTime()).pin(pin).build()).build();
        } else {
            otpLimit = optionalOtpLimit.get();
            isLastPerRoundWrongAttempt =
                    otpLimitConfig.getPerRoundAttempt().compareTo(otpLimit.getPerRoundAttempt() + 1) == 0;
            isLastWrongRoundAttempt = otpLimitConfig.getFailRound().compareTo(otpLimit.getFailRound()) == 0;
            otpLimit.setPerRoundAttempt(otpLimit.getPerRoundAttempt() + 1);
            if (isLastPerRoundWrongAttempt) {
                if (isLastWrongRoundAttempt) {
                    otpLimit.setFailRound(otpLimitConfig.getFailRound());
                    otpLimit.setPerRoundAttempt(otpLimitConfig.getPerRoundAttempt());
                } else {
                    otpLimit.setFailRound(otpLimit.getFailRound() + 1);
                }
                lockTimeInMinute = calculateLockTimeInMinute(otpLimitConfig, otpLimit);
                otpLimit.setLockedTo(DateUtils.addMinutes(TimeUtil.getCurrentDateTime(), lockTimeInMinute));
            } else {
                otpLimit.setLockedTo(null);
                otpLimit.setFailRound(otpLimit.getFailRound());
            }

        }
        otpLimit.getEmbeddedLimit().setLastAttempt(TimeUtil.getCurrentDateTime());
        otpLimitRepository.save(otpLimit);
        int availableAttemptCount = otpLimitConfig.getPerRoundAttempt() - otpLimit.getPerRoundAttempt();
        ExceptionUtil.throwOtpLimitException(isLastPerRoundWrongAttempt, isLastWrongRoundAttempt,
                availableAttemptCount, lockTimeInMinute);
    }

    private int calculateLockTimeInMinute(OtpLimitConfig otpLimitConfig, OtpLimit otpLimit) {
        return Integer.parseInt(otpLimitConfig.getRoundLockTime().split("#")[otpLimit.getFailRound() - 1]);
    }


    @Override
    public void doSuccessAttempt(String pin, LimitServiceTypeEnum serviceName) {
        Optional<OtpLimit> optionalOtpLimit =
                otpLimitRepository.findByPinAndService(pin, serviceName.getDbCode());
        if (optionalOtpLimit.isPresent()) {
            OtpLimit otpLimit = optionalOtpLimit.get();
            otpLimit.setFailRound(0);
            otpLimit.setPerRoundAttempt(0);
            otpLimit.getEmbeddedLimit().setLastAttempt(TimeUtil.getCurrentDateTime());
            otpLimit.setLockedTo(null);
            otpLimitRepository.save(otpLimit);
        }
    }

    public void checkOtpLimit(Optional<OtpLimit> optionalOtpLimit) {
        if (optionalOtpLimit.isPresent()) {
            OtpLimit otpLimit = optionalOtpLimit.get();
            checkOtpBlock(otpLimit);
        }
    }

    private void checkOtpBlock(OtpLimit otpLimit) {
        boolean isLastRoundOtp = otpLimit.getFailRound()
                .compareTo(otpLimit.getOtpLimitConfig().getFailRound()) == 0;
        boolean isTemporaryBlocked =
                otpLimit.getPerRoundAttempt()
                        .compareTo(otpLimit.getOtpLimitConfig().getPerRoundAttempt()) == 0;
        if (isLastRoundOtp && isTemporaryBlocked) {
            throw new BadRequestException(BadRequestErrorEnum.PERMANENTLY_BLOCKED_OTP);
        } else if (isTemporaryBlocked) {
            throw new BadRequestException(BadRequestErrorEnum.TEMPORARY_BLOCKED_OTP);
        }
    }

}
