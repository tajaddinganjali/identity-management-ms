package com.management.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CurrentDeviceDTO {

    private String id;
    private String deviceModel;
    private String deviceOs;
    private String deviceOsVersion;
    private String appVersion;
    private String oneSignalToken;
    private boolean jailyBroken;
    private String deviceCode;
    private String deviceNo;
    private boolean pushEnabled;

}
