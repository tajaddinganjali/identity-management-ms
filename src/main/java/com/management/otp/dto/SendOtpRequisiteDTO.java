package com.management.otp.dto;

import com.management.otp.enums.OtpLimitTypeEnum;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOtpRequisiteDTO {

    private String pin;
    private Map<String, Object> data;
    private String otpDefinitionId;
    private String phone;
    @Builder.Default
    private OtpLimitTypeEnum otpLimitTypeEnum = OtpLimitTypeEnum.NO_LIMIT;

}
