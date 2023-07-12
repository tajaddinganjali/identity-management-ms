package com.management.otp.dto;

import com.management.exception.ErrorCodes;
import com.management.logging.Log;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("Wrapper for the user phone number.")
@Log
public class UserPhoneDTO {

    @ApiModelProperty(value = "Phone number of the user to be registered.", example = "994503078765", required = true)
    @Pattern(regexp = "^994(10|50|51|55|60|99|70|77).{7}$", message = ErrorCodes.INVALID_PHONE_NUMBER_FORMAT_IM)
    private String phone;

}
