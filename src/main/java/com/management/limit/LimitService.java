package com.management.limit;


import com.management.limit.enums.LimitServiceTypeEnum;
import java.util.EnumMap;
import java.util.List;

public interface LimitService {

    void permanentlyBlock(String pin);

    void checkPermanentlyBlock(String pin);

    void checkLimit(EnumMap<LimitServiceTypeEnum, String> checkLimitMap);

    void resetScheduledWrongAttempt(List<LimitServiceTypeEnum> serviceNames);

    void doWrongAttempt(String value, LimitServiceTypeEnum serviceName);

    void doSuccessAttempt(EnumMap<LimitServiceTypeEnum, String> checkLimitMap);

    boolean hasAttempt(String pin, String phone);

}
