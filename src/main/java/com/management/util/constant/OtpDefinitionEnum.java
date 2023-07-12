package com.management.util.constant;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.register.enums.ConsumerTypeEnum;
import lombok.Getter;

@Getter
public enum OtpDefinitionEnum {
    MOBILE_REGISTRATION_OTP_DEFINITION_ID("15eb6eeb-53d5-4653-bf1b-1db1c89946d0"),
    WEB_REGISTRATION_OTP_DEFINITION_ID("b6a99312-b8fa-029c-e053-0a8006141f33");

    private String definitionId;

    OtpDefinitionEnum(String definitionId) {
        this.definitionId = definitionId;
    }


    public static String getOtpDefinitionId(ConsumerTypeEnum consumerType) {
        switch (consumerType) {
            case MOBILE:
                return OtpDefinitionEnum.MOBILE_REGISTRATION_OTP_DEFINITION_ID.getDefinitionId();
            case WEB:
                return OtpDefinitionEnum.WEB_REGISTRATION_OTP_DEFINITION_ID.getDefinitionId();
            default:
                throw new BadRequestException(BadRequestErrorEnum.INVALID_CONSUMER);
        }
    }

}
