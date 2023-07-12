package com.management.register.dto.internal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.management.logging.Log;
import com.management.logging.LogIgnore;
import java.io.Serializable;
import lombok.Data;

@Data
@Log
public class DeviceInfoDTO implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    @JsonProperty("device_model")
    private String deviceModel;
    @JsonProperty("device_os")
    private String deviceOs;
    @JsonProperty("device_os_version")
    private String deviceOsVersion;
    @JsonProperty("device_code")
    private String deviceCode;
    @JsonProperty("device_no")
    private String deviceNo;
    @JsonProperty("device_fingerprint")
    private String deviceFingerprint;
    @JsonProperty("app_version")
    private String appVersion;
    @JsonProperty("fcm_token")
    @JsonAlias("token")
    @LogIgnore
    private String fcmToken;
    @LogIgnore
    @JsonProperty("one_signal_token")
    private String oneSignalToken;
    @JsonProperty("jaily_broken")
    private boolean jailyBroken;
    @JsonProperty("push_enabled")
    private boolean pushEnabled;

}
