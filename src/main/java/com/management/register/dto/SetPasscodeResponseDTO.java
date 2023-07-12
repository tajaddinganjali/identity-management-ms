package com.management.register.dto;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@Builder
public class SetPasscodeResponseDTO {

    @JsonProperty
    private String userId;
    private String name;

}
