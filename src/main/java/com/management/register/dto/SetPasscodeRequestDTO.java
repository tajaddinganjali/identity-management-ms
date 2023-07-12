package com.management.register.dto;

import com.management.exception.ErrorCodes;
import com.management.util.validation.ValidPasscode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPasscodeRequestDTO {

    @JsonProperty
    @ValidPasscode(message = ErrorCodes.INVALID_PASSCODE)
    private String passcode;

    @com.fasterxml.jackson.annotation.JsonProperty("agreementSigned")
    private boolean agreementSigned;

}
