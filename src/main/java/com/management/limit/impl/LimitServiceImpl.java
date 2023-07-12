package com.management.limit.impl;

import com.ibam.errorhandling.IBAMBaseException;
import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.ErrorCodes;
import com.management.exception.NotFoundErrorEnum;
import com.management.limit.LimitService;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.limit.strategy.LimitServiceStrategy;
import com.management.model.RegisterLimitAud;
import com.management.model.User;
import com.management.model.enums.UserStatus;
import com.management.repository.RegisterLimitAuditRepository;
import com.management.repository.UserRepository;
import com.management.util.TimeUtil;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {

    private final Map<String, LimitServiceStrategy> limitServiceStrategies;

    private final UserRepository userRepository;

    private final RegisterLimitAuditRepository registerLimitAuditRepository;

    @Value("${register-limit.attempt-time-in-hour}")
    private Integer registerLimitAttemptTimeInHour;

    private static List<String> permanentlyBlockedCodes() {
        return Arrays.asList(ErrorCodes.PERMANENTLY_BLOCKED, ErrorCodes.PERMANENTLY_BLOCKED_OTP);
    }

    @Override
    public void permanentlyBlock(String pin) {
        User user = userRepository.findByPin(pin)
                .orElseThrow(() -> new BadRequestException(NotFoundErrorEnum.USER_NOT_FOUND));
        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
    }

    @Override
    public void checkPermanentlyBlock(String pin) {
        Optional<User> user = userRepository.findByPin(pin);
        if (user.isPresent() && user.get().getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException(BadRequestErrorEnum.PERMANENTLY_BLOCKED);
        }
    }

    @Override
    public void checkLimit(EnumMap<LimitServiceTypeEnum, String> checkLimitMap) {
        checkLimitMap.forEach((serviceName, value) -> limitServiceStrategies
                .get(serviceName.getServiceStrategy()).checkLimit(value, serviceName));
    }

    @Override
    public void doWrongAttempt(String value, LimitServiceTypeEnum serviceName) {
        try {
            limitServiceStrategies.get(serviceName.getServiceStrategy()).doWrongAttempt(value, serviceName);
        } catch (IBAMBaseException exception) {
            if (permanentlyBlockedCodes().contains(exception.getCode())) {
                permanentlyBlock(value);
            }
            throw exception;
        }
    }

    @Override
    public void doSuccessAttempt(EnumMap<LimitServiceTypeEnum, String> checkLimitMap) {
        checkLimitMap.forEach((serviceName, value) -> limitServiceStrategies
                .get(serviceName.getServiceStrategy()).doSuccessAttempt(value, serviceName));
    }

    @Override
    public void resetScheduledWrongAttempt(List<LimitServiceTypeEnum> serviceNames) {
        serviceNames.forEach(serviceName -> limitServiceStrategies.get(serviceName.getServiceStrategy())
                .resetScheduledWrongAttempt());
    }

    @Override
    public boolean hasAttempt(String pin, String phone) {
        Date date = DateUtils.addHours(TimeUtil.getCurrentDateTime(), -registerLimitAttemptTimeInHour);
        List<RegisterLimitAud> audList =
                registerLimitAuditRepository.findAllByPinAndPhoneAndCreatedDateAfter(pin, phone, date);
        return !audList.isEmpty();
    }

}
