package com.management.limit.enums;

import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import lombok.Getter;

@Getter
public enum LimitServiceTypeEnum {
    REGISTER_WITH_PIN_OTP(1, "register_with_pin_otp"),
    REGISTER_WITH_SAME_PIN(2, "register_with_same_pin"),
    REGISTER_WITH_SAME_PHONE(3, "register_with_same_phone"),
    REGISTER_AUTHORIZATION_WITH_SMS(4, "register_authorization"),
    LOGIN_CHECK_PASSCODE(0, "login_check_passcode");

    private final String serviceStrategy;
    private final Integer dbCode;

    LimitServiceTypeEnum(int dbCode, String serviceStrategy) {
        this.dbCode = dbCode;
        this.serviceStrategy = serviceStrategy;
    }

    public static LimitServiceTypeEnum findByDbCode(Integer dbCode) {

        return Arrays.stream(LimitServiceTypeEnum.values())
                .filter(limitServiceTypeEnum -> limitServiceTypeEnum.getDbCode().compareTo(dbCode) == 0)
                .findFirst()
                .orElseThrow(() -> new GeneralTechnicalException(GeneralTechErrorEnum.SERVER_ERROR));

    }

    public static List<LimitServiceTypeEnum> getRegisterAttempts() {
        List<LimitServiceTypeEnum> limitServiceTypes = new ArrayList<>();
        limitServiceTypes.add(LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN);
        limitServiceTypes.add(LimitServiceTypeEnum.REGISTER_WITH_SAME_PHONE);
        return limitServiceTypes;
    }

    public static List<LimitServiceTypeEnum> getAuthorizationAttempts() {
        List<LimitServiceTypeEnum> limitServiceTypes = new ArrayList<>();
        limitServiceTypes.add(LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS);
        return limitServiceTypes;
    }

    public static List<LimitServiceTypeEnum> getOtpAttempts() {
        List<LimitServiceTypeEnum> limitServiceTypes = new ArrayList<>();
        limitServiceTypes.add(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP);
        return limitServiceTypes;
    }

    public static EnumMap<LimitServiceTypeEnum, String> getEnumMap(List<LimitServiceTypeEnum> limitServiceTypeEnums,
                                                                   String value) {
        EnumMap<LimitServiceTypeEnum, String> enumMap =
                new EnumMap<>(LimitServiceTypeEnum.class);
        limitServiceTypeEnums.forEach(limitServiceTypeEnum -> enumMap.put(limitServiceTypeEnum, value));
        return enumMap;
    }


    public static EnumMap<LimitServiceTypeEnum, String> getCheckLimitMap(
            RegisterRequisiteDTO registerRequisite) {
        EnumMap<LimitServiceTypeEnum, String> checkMap = new EnumMap<>(LimitServiceTypeEnum.class);
        checkMap.put(LimitServiceTypeEnum.REGISTER_WITH_SAME_PIN, registerRequisite.getRegisterPinRequest().getPin());
        checkMap.put(LimitServiceTypeEnum.REGISTER_WITH_SAME_PHONE,
                registerRequisite.getRegisterPinRequest().getPhone());
        checkMap.put(LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS,
                registerRequisite.getRegisterPinRequest().getPhone());
        checkMap.put(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP, registerRequisite.getRegisterPinRequest().getPin());
        return checkMap;
    }
}
