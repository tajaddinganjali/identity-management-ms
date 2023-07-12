package com.management.otp.dto;

import com.management.exception.ErrorCodes;
import com.management.logging.Log;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Log
public class VerifyOtpRequestDTO {

    @ApiModelProperty(value = "OTP code", example = "123456", required = true)
    @NotNull(message = ErrorCodes.INVALID_OTP_FORMAT_IM)
    @Pattern(regexp = "^\\d{6}$", message = ErrorCodes.INVALID_OTP_FORMAT_IM)
    private String otp;

}
