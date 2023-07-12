package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.management.logging.Log;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Log
@Builder
public class VerifyOtpResponseDTO {

    @JsonProperty
    private String userId;
    @JsonProperty
    private Boolean isValid;
    @org.codehaus.jackson.annotate.JsonProperty("registration_token")
    @ApiModelProperty(value = "registration_token", notes = "Registration token will be used for setting passcode.")
    private String registrationToken;

}
