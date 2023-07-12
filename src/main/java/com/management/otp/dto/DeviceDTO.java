package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeviceDTO implements Comparable<DeviceDTO> {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("device_model")
    private String deviceModel;
    @JsonProperty("device_os")
    private String deviceOs;
    @JsonProperty("device_os_version")
    private String deviceOsVersion;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private ZonedDateTime lastLogin;
    @JsonProperty("is_online")
    private Boolean isOnline;

    @Override
    public int compareTo(DeviceDTO device) {
        return device.lastLogin.compareTo(lastLogin);
    }

}
