package com.management.otp.service;

import com.management.model.Device;
import com.management.model.User;
import com.management.otp.dto.DeviceLimitDTO;
import com.management.otp.dto.GetDevicesResponseDTO;
import com.management.register.dto.internal.DeviceInfoDTO;
import java.util.List;

public interface DeviceService {

    Device saveOrUpdateForRegistration(DeviceInfoDTO deviceInfo, User user);

    Device saveOrUpdateForToken(DeviceInfoDTO deviceInfo, User user);

    GetDevicesResponseDTO getDevices(String userId, String deviceId);

    void updateLastActiveDate(Device device);

    void deleteDevice(String accessToken, String deviceId);

    List<DeviceLimitDTO> getDevicesByUser(User user);


}
