package com.management.limit.strategy;

import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.limit.enums.LimitServiceTypeEnum;

public interface LimitServiceStrategy {

    void checkLimit(String value, LimitServiceTypeEnum serviceName);

    default void resetScheduledWrongAttempt() {
        throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "method not implemented");
    }

    default void doWrongAttempt(String value, LimitServiceTypeEnum serviceName) {
        throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "method not implemented");
    }

    void doSuccessAttempt(String value, LimitServiceTypeEnum serviceName);

    default boolean hasWrongAttempt(String value) {
        throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "method not implemented");
    }

}
