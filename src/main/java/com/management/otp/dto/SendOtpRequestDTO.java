package com.management.otp.dto;

import com.management.logging.Log;
import com.management.otp.enums.OtpDefinitionTypeEnum;
import com.management.util.constant.OtpDefinitionEnum;
import io.swagger.annotations.ApiModel;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("Wrapper for the send OTPs request")
@Log
public class SendOtpRequestDTO {

    private String consumerId;
    private String lang;
    private String userId;
    private String pin;
    private String phone;
    private String cif;
    private String otp;
    private OtpDefinitionTypeEnum definitionType;
    @Builder.Default
    private OtpDefinitionEnum otpDefinitionId = OtpDefinitionEnum.MOBILE_REGISTRATION_OTP_DEFINITION_ID;
    @JsonProperty("data")
    private Map<String, Object> data;

}
