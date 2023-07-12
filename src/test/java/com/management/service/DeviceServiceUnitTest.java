package com.management.service;

import com.management.exception.BadRequestException;
import com.management.identity.service.UserService;
import com.management.model.Device;
import com.management.model.User;
import com.management.otp.service.SessionService;
import com.management.otp.service.impl.DeviceServiceImpl;
import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.register.enums.DeviceStatus;
import com.management.repository.DeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeviceServiceUnitTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    void saveOrUpdateForRegistration() {
        Mockito.when(deviceRepository.findFirstByUserAndDeviceCode(Mockito.any(), Mockito.anyString()))
                .thenReturn(Optional.of(Device.builder().build()));
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.saveOrUpdateForRegistration(getDeviceInfo(), User.builder().build()));
    }

    @Test
    void saveOrUpdateForRegistration_noDevice() {
        Mockito.when(deviceRepository.findFirstByUserAndDeviceCode(Mockito.any(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.saveOrUpdateForRegistration(getDeviceInfo(), User.builder().build()));
    }

    @Test
    void saveOrUpdateForToken() {
        Mockito.when(deviceRepository.findFirstByUserAndDeviceNo(Mockito.any(), Mockito.anyString()))
                .thenReturn(Optional.of(Device.builder().build()));
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.saveOrUpdateForToken(getDeviceInfo(), User.builder().build()));
    }

    @Test
    void saveOrUpdateForToken_noDevice() {
        Mockito.when(deviceRepository.findFirstByUserAndDeviceNo(Mockito.any(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.findFirstByUserAndDeviceCode(Mockito.any(), Mockito.anyString()))
                .thenReturn(Optional.of(Device.builder().build()));
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.saveOrUpdateForToken(getDeviceInfo(), User.builder().build()));
    }

    @Test
    void saveOrUpdateForToken_noDeviceCred() {
        Mockito.when(deviceRepository.findFirstByUserAndDeviceNo(Mockito.any(), Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.findFirstByUserAndDeviceCode(Mockito.any(), Mockito.anyString()))
                .thenReturn(Optional.empty());
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.saveOrUpdateForToken(getDeviceInfo(), User.builder().build()));
    }

    @Test
    void getDevices() {
        Mockito.when(userService.findActiveUserById(Mockito.anyString())).thenReturn(User.builder().build());
        Mockito.when(deviceRepository.findDevicesByUser(Mockito.any())).thenReturn(List.of(getDevice()));
        Assertions.assertDoesNotThrow(() -> deviceService.getDevices("9092a293-caaf-4c26-94ea-679a9ffef7a0", "9092a293-caaf-4c26-94ea-679a9ffef7a0"));
    }

    @Test
    void updateLastActiveDate() {
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Assertions.assertDoesNotThrow(() -> deviceService.updateLastActiveDate(getDevice()));
    }

    @Test
    void deleteDevice() {
        Mockito.when(sessionService.getUserIdFromToken(Mockito.anyString())).thenReturn("token");
        Mockito.when(userService.findActiveUserById(Mockito.anyString())).thenReturn(User.builder().build());
        Mockito.when(deviceRepository.findDeviceByIdAndUserAndDeviceStatus(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(getDevice()));
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(Device.builder().build());
        Mockito.doNothing().when(sessionService).deactivateActiveSesssionsByUserAndDevice(Mockito.any(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> deviceService.deleteDevice("token", getDevice().getId().toString()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "device_id"})
    void deleteDevice_exception(String deviceId) {
        Assertions.assertThrows(BadRequestException.class, () -> deviceService.deleteDevice("token", deviceId));
    }

    @Test
    void getDevicesByUser() {
        Mockito.when(deviceRepository.findDevicesByUserAndDeviceStatus(Mockito.any(), Mockito.any())).thenReturn(List.of(getDevice()));
        Assertions.assertDoesNotThrow(() -> deviceService.getDevicesByUser(User.builder().build()));
    }

    private DeviceInfoDTO getDeviceInfo() {
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setDeviceModel("model");
        deviceInfoDTO.setDeviceNo("no");
        deviceInfoDTO.setDeviceCode("code");
        deviceInfoDTO.setFcmToken("token");
        deviceInfoDTO.setOneSignalToken("token");
        deviceInfoDTO.setAppVersion("version");
        deviceInfoDTO.setDeviceOs("os");
        deviceInfoDTO.setDeviceOsVersion("version");
        deviceInfoDTO.setJailyBroken(true);
        deviceInfoDTO.setPushEnabled(true);
        deviceInfoDTO.setDeviceFingerprint("finger");
        return deviceInfoDTO;
    }

    private Device getDevice() {
        return Device.builder()
                .id(UUID.randomUUID())
                .deviceStatus(DeviceStatus.ACTIVE)
                .lastActiveDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .deviceModel("model")
                .deviceNo("no")
                .deviceOs("os")
                .deviceOsVersion("version")
                .build();
    }

}
