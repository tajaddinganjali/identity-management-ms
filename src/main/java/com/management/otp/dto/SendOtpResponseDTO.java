package com.management.otp.dto;

import com.management.logging.Log;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Log
public class SendOtpResponseDTO {

    @ApiModelProperty(value = "verification token for otp", required = true)
    private String verificationToken;
    private boolean isLastSms;

}
