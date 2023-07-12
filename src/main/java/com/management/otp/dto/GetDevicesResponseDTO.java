package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDevicesResponseDTO {

    @JsonProperty("current_device")
    private DeviceDTO currentDevice;
    @JsonProperty("other_devices")
    private List<DeviceDTO> otherDevices;

}
