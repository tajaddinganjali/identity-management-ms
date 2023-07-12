package com.management.register.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ConsumerTypeEnum {
    MOBILE("mobile"),
    WEB("web");

    private static final String OTP = "otp_";
    private final String consumerId;

    ConsumerTypeEnum(String consumerId) {
        this.consumerId = consumerId;
    }

    public static ConsumerTypeEnum findByNameOrElseIbaMobile(String channelName) {
        if (StringUtils.isBlank(channelName)) {
            return ConsumerTypeEnum.MOBILE;
        }
        return Arrays.stream(ConsumerTypeEnum.values())
                .filter(e -> e.getConsumerId().equalsIgnoreCase(channelName))
                .findFirst().orElse(ConsumerTypeEnum.MOBILE);
    }


    public static String getRegisterStrategy(ConsumerTypeEnum consumerType) {
        return consumerType.name().toLowerCase();
    }

    public static String getOtpStrategy(ConsumerTypeEnum consumerType) {
        return OTP.concat(consumerType.name().toLowerCase());
    }

    @JsonCreator
    public static ConsumerTypeEnum forValue(String string) {
        return findByNameOrElseIbaMobile(string);
    }

}
