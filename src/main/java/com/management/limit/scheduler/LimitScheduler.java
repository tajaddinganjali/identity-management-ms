package com.management.limit.scheduler;

import com.management.limit.LimitService;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.util.constant.ExecutorConstant;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LimitScheduler {

    private final LimitService limitService;

    @Scheduled(cron = "${iam-svc.reset-wrong-otp-attempt}")
    @SchedulerLock(name = ExecutorConstant.RESET_WRONG_OTP_ATTEMPT, lockAtMostFor = "35m",
            lockAtLeastFor = "${iam-svc.reset-wrong-otp-attempt-least}")
    public void resetPerRoundWrongOtpAttempt() {
        limitService.resetScheduledWrongAttempt(LimitServiceTypeEnum.getOtpAttempts());
    }

    @Scheduled(cron = "${iam-svc.reset-authorization-attempt}")
    @SchedulerLock(name = ExecutorConstant.RESET_AUTHORIZATION_ATTEMPT, lockAtMostFor = "35m",
            lockAtLeastFor = "${iam-svc.reset-authorization-attempt-least}")
    public void resetAuthorizationAttempt() {
        limitService.resetScheduledWrongAttempt(LimitServiceTypeEnum.getAuthorizationAttempts());
    }


    @Scheduled(cron = "${iam-svc.reset-wrong-register-attempt}")
    @SchedulerLock(name = ExecutorConstant.RESET_WRONG_REGISTER_SAME_PHONE_ATTEMPT, lockAtMostFor = "35m",
            lockAtLeastFor = "${iam-svc.reset-wrong-register-attempt-least}")
    public void resetWrongRegisterSamePhoneAttempt() {
        limitService.resetScheduledWrongAttempt(LimitServiceTypeEnum.getRegisterAttempts());
    }


}
