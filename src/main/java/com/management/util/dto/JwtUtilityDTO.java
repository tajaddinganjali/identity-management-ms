package com.management.util.dto;

import com.management.identity.enums.ProfileType;
import com.management.register.enums.ConsumerTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtUtilityDTO {

    private String userId;
    private String sessionId;
    private ConsumerTypeEnum consumerTypeEnum;
    private String deviceId;
    private String phone;
    private String subject;
    private ProfileType profileType;
    private String tokenType;

}
