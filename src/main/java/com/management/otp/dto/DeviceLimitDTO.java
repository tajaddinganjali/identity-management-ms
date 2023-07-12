package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeviceLimitDTO {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("device_model")
    private String deviceModel;
    @JsonProperty("device_os")
    private String deviceOs;
    @JsonProperty("device_os_version")
    private String deviceOsVersion;

}
