package com.management.scheduler;

import com.management.limit.LimitService;
import com.management.limit.scheduler.LimitScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LimitSchedulerUnitTest {

    @Mock
    private LimitService limitService;

    @InjectMocks
    private LimitScheduler limitScheduler;

    @Test
    void resetWrongOtpAttempt() {
        Mockito.doNothing().when(limitService).resetScheduledWrongAttempt(ArgumentMatchers.any());
        Assertions.assertDoesNotThrow(() -> limitScheduler.resetPerRoundWrongOtpAttempt());
    }

    @Test
    void resetWrongRegisterSamePhoneAttempt() {
        Mockito.doNothing().when(limitService).resetScheduledWrongAttempt(ArgumentMatchers.any());
        Assertions.assertDoesNotThrow(() -> limitScheduler.resetWrongRegisterSamePhoneAttempt());
    }

    @Test
    void resetAuthorizationAttempt() {
        Mockito.doNothing().when(limitService).resetScheduledWrongAttempt(ArgumentMatchers.any());
        Assertions.assertDoesNotThrow(() -> limitScheduler.resetAuthorizationAttempt());
    }

}
