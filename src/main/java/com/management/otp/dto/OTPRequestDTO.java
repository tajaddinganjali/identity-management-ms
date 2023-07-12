package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.management.otp.enums.OtpLimitTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDTO {

    private SmsDTO sms;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("service_type")
    private OtpLimitTypeEnum otpLimitType;

}
