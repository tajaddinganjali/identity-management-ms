package com.management.otp.service.impl;

import static com.management.util.constant.TokenConstants.JWT_PREFIX;
import static java.util.stream.Collectors.toList;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.identity.service.UserService;
import com.management.model.Device;
import com.management.model.User;
import com.management.otp.dto.DeviceDTO;
import com.management.otp.dto.DeviceLimitDTO;
import com.management.otp.dto.GetDevicesResponseDTO;
import com.management.otp.service.DeviceService;
import com.management.otp.service.SessionService;
import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.register.enums.DeviceStatus;
import com.management.repository.DeviceRepository;
import com.management.util.TimeUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserService userService;
    private final SessionService sessionService;

    @Override
    public Device saveOrUpdateForRegistration(DeviceInfoDTO deviceInfo, User user) {
        Optional<Device> optionalDeviceEnt = deviceRepository.findFirstByUserAndDeviceCode(user,
                deviceInfo.getDeviceCode());
        Device device;
        if (optionalDeviceEnt.isPresent()) {
            device = optionalDeviceEnt.get();
            updateDeviceForRegistration(device, deviceInfo);
            deviceRepository.save(device);
        } else {
            device = convertDeviceRequestToEntity(deviceInfo, user);
            if (Objects.nonNull(device)) {
                deviceRepository.save(device);
            }
        }
        return device;
    }

    @Override
    public Device saveOrUpdateForToken(DeviceInfoDTO deviceRequest, User user) {
        Optional<Device> optionalDeviceEnt = deviceRepository.findFirstByUserAndDeviceNo(user,
                deviceRequest.getDeviceNo());
        Device device;
        if (optionalDeviceEnt.isPresent()) {
            device = optionalDeviceEnt.get();
            updateDevice(device, deviceRequest);
            updateLastLoginAndLastActiveDate(device);
        } else {
            optionalDeviceEnt = deviceRepository.findFirstByUserAndDeviceCode(user, deviceRequest.getDeviceCode());
            if (optionalDeviceEnt.isPresent()) {
                device = optionalDeviceEnt.get();
                updateDevice(device, deviceRequest);
                updateLastLoginAndLastActiveDate(device);
            } else {
                device = convertDeviceRequestToEntity(deviceRequest, user);
                device.setDeviceStatus(DeviceStatus.ACTIVE);
                updateLastLoginAndLastActiveDate(device);
            }
        }
        return deviceRepository.save(device);
    }

    private Device convertDeviceRequestToEntity(DeviceInfoDTO deviceRequest,
                                                User user) {
        return Device.builder()
                .deviceStatus(DeviceStatus.REGISTERING)
                .deviceModel(deviceRequest.getDeviceModel())
                .deviceOs(deviceRequest.getDeviceOs())
                .deviceOsVersion(deviceRequest.getDeviceOsVersion())
                .deviceCode(deviceRequest.getDeviceCode())
                .deviceNo(deviceRequest.getDeviceNo())
                .deviceFingerprint(deviceRequest.getDeviceFingerprint())
                .appVersion(deviceRequest.getAppVersion())
                .fcmToken(deviceRequest.getFcmToken())
                .oneSignalToken(deviceRequest.getOneSignalToken())
                .jailyBroken(deviceRequest.isJailyBroken())
                .pushEnabled(deviceRequest.isPushEnabled())
                .user(user)
                .build();
    }

    private void updateDeviceForRegistration(Device deviceFoundFromDB, DeviceInfoDTO deviceRequest) {
        deviceFoundFromDB.setDeviceStatus(DeviceStatus.ACTIVE);
        deviceFoundFromDB.setDeviceModel(deviceRequest.getDeviceModel());
        deviceFoundFromDB.setDeviceCode(deviceRequest.getDeviceCode());
        deviceFoundFromDB.setFcmToken(deviceRequest.getFcmToken());
        deviceFoundFromDB.setOneSignalToken(deviceRequest.getOneSignalToken());
        deviceFoundFromDB.setAppVersion(deviceRequest.getAppVersion());
        deviceFoundFromDB.setDeviceOs(deviceRequest.getDeviceOs());
        deviceFoundFromDB.setDeviceOsVersion(deviceRequest.getDeviceOsVersion());
        deviceFoundFromDB.setJailyBroken(deviceRequest.isJailyBroken());
        deviceFoundFromDB.setPushEnabled(deviceRequest.isPushEnabled());
        deviceFoundFromDB.setDeviceFingerprint(deviceRequest.getDeviceFingerprint());
    }

    private void updateDevice(Device deviceFoundFromDB, DeviceInfoDTO deviceRequest) {
        deviceFoundFromDB.setDeviceStatus(DeviceStatus.ACTIVE);
        deviceFoundFromDB.setDeviceModel(deviceRequest.getDeviceModel());
        deviceFoundFromDB.setDeviceNo(deviceRequest.getDeviceNo());
        deviceFoundFromDB.setDeviceCode(deviceRequest.getDeviceCode());
        deviceFoundFromDB.setFcmToken(deviceRequest.getFcmToken());
        deviceFoundFromDB.setOneSignalToken(deviceRequest.getOneSignalToken());
        deviceFoundFromDB.setAppVersion(deviceRequest.getAppVersion());
        deviceFoundFromDB.setDeviceOs(deviceRequest.getDeviceOs());
        deviceFoundFromDB.setDeviceOsVersion(deviceRequest.getDeviceOsVersion());
        deviceFoundFromDB.setJailyBroken(deviceRequest.isJailyBroken());
        deviceFoundFromDB.setPushEnabled(deviceRequest.isPushEnabled());
        deviceFoundFromDB.setDeviceFingerprint(deviceRequest.getDeviceFingerprint());
    }

    private void updateLastLoginAndLastActiveDate(Device device) {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        device.setLastLoginDate(dateTimeNow);
        device.setLastActiveDate(dateTimeNow);
    }

    @Override
    public GetDevicesResponseDTO getDevices(String userId, String deviceId) {
        User user = userService.findActiveUserById(userId);
        List<Device> devices = deviceRepository.findDevicesByUser(user)
                .stream()
                .filter(deviceEnt -> deviceEnt.getDeviceStatus().equals(DeviceStatus.ACTIVE)
                        && deviceEnt.getLastLoginDate() != null)
                .collect(toList());

        DeviceDTO getCurrentDevice = null;
        List<DeviceDTO> getOtherDevicesList = new ArrayList<>();

        for (Device device : devices) {
            LocalDateTime localDateTime =
                    (device.getLastActiveDate() != null) ? device.getLastActiveDate() : device.getLastLoginDate();

            ZoneId zoneId = ZoneId.of("Asia/Baku");
            ZonedDateTime zonedDateTime = TimeUtil.convertUTCToZoneDateTime(localDateTime, zoneId);

            boolean isOnline = (LocalDateTime.now().atZone(ZoneId.of("UTC")).toEpochSecond()
                    - localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond()) < 5 * 60;

            if (device.getId().equals(UUID.fromString(deviceId))) {
                getCurrentDevice = entityToDto(device, zonedDateTime, isOnline);
            } else {
                getOtherDevicesList.add(entityToDto(device, zonedDateTime, isOnline));
            }
        }

        Collections.sort(getOtherDevicesList);

        return GetDevicesResponseDTO.builder()
                .currentDevice(getCurrentDevice)
                .otherDevices(getOtherDevicesList)
                .build();
    }

    private DeviceDTO entityToDto(Device device, ZonedDateTime dateTime, boolean isOnline) {
        return DeviceDTO
                .builder()
                .id(device.getId())
                .deviceModel(device.getDeviceModel())
                .deviceOs(device.getDeviceOs())
                .deviceOsVersion(device.getDeviceOsVersion())
                .isOnline(isOnline)
                .lastLogin(dateTime)
                .build();
    }

    @Override
    public void updateLastActiveDate(Device device) {
        device.setLastActiveDate(LocalDateTime.now());
        deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(String accessToken, String deviceId) {
        UUID deviceIdUuid;
        if (Objects.isNull(deviceId) || StringUtils.isBlank(deviceId)) {
            throw new BadRequestException(BadRequestErrorEnum.DEVICE_CANNOT_BE_NULL);
        } else {
            try {
                deviceIdUuid = UUID.fromString(deviceId);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(BadRequestErrorEnum.INVALID_UUID_FORMAT);
            }
        }
        accessToken = accessToken.replace(JWT_PREFIX, "");
        String userId = sessionService.getUserIdFromToken(accessToken);
        User user = userService.findActiveUserById(userId);
        Optional<Device> deviceOptional = deviceRepository.findDeviceByIdAndUserAndDeviceStatus(deviceIdUuid,
                user, DeviceStatus.ACTIVE);
        Device device = deviceOptional.orElseThrow(() -> new BadRequestException(BadRequestErrorEnum.DEVICE_NOT_FOUND));
        if (Objects.nonNull(device)
                && StringUtils.isNotBlank(device.getDeviceNo())) {
            device.setDeviceStatus(DeviceStatus.DELETED);
            deviceRepository.save(device);
        }
        sessionService.deactivateActiveSesssionsByUserAndDevice(user, device);
    }

    @Override
    public List<DeviceLimitDTO> getDevicesByUser(User user) {
        List<Device> devices = deviceRepository.findDevicesByUserAndDeviceStatus(user, DeviceStatus.ACTIVE);

        return devices.stream()
                .map(device -> DeviceLimitDTO
                        .builder()
                        .id(device.getId())
                        .deviceOs(device.getDeviceOs())
                        .deviceModel(device.getDeviceModel())
                        .deviceOsVersion(device.getDeviceOsVersion())
                        .build())
                .collect(toList());
    }

}
