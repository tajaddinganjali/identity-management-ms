package com.management.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DeviceDTO {

    private CurrentDeviceDTO currentDevice;

}
