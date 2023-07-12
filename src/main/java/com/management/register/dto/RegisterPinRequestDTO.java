package com.management.register.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.management.exception.ErrorCodes;
import com.management.logging.Log;
import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.util.validation.ValidPin;
import io.swagger.annotations.ApiModelProperty;
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
public class RegisterPinRequestDTO {

    @ApiModelProperty(value = "Phone number of the user to be registered.", example = "994503078765", required = true)
    @Pattern(regexp = "^994(10|50|51|55|60|99|70|77).{7}$", message = ErrorCodes.INVALID_PHONE_NUMBER_FORMAT_IM)
    private String phone;

    @ApiModelProperty(value = "Pin number of user's identity card.", example = "5CZ5SR8", required = true)
    @ValidPin(message = ErrorCodes.INVALID_PIN_FORMAT_IM)
    private String pin;

    @JsonProperty("birth_date")
    @ApiModelProperty("Date of birth for of the user to be registered.")
    private String birthDate;

    @ApiModelProperty("Register device info")
    private DeviceInfoDTO device;

}
