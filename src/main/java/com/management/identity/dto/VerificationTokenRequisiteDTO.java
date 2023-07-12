package com.management.identity.dto;

import com.management.identity.enums.ProfileType;
import com.management.register.enums.ConsumerTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationTokenRequisiteDTO {

    private String userId;
    private ConsumerTypeEnum consumer;
    private ProfileType profileType;
    @Builder.Default
    private String deviceId = "";
    @Builder.Default
    private String phone = "";

}
