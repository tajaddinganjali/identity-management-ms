package com.management.identity.service.impl;

import com.management.identity.dto.PushNotificationRequestDTO;
import com.management.identity.service.PushNotificationService;
import com.management.model.Device;
import com.management.model.User;
import com.management.register.enums.DeviceStatus;
import com.management.repository.DeviceRepository;
import com.management.repository.NotificationRepository;
import com.management.util.constant.PropertyConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private final NotificationRepository notificationRepository;
    private final DeviceRepository deviceRepository;

    @Override
    public void sendPushNotification(User user, Device device) {

        Map<String, Object> data = new HashMap<>();
        String deviceName = device.getDeviceModel() + " : " + device.getDeviceOsVersion();
        data.put("device_name", deviceName);

        List<Device> devices = deviceRepository.findDevicesByUserAndDeviceStatus(user, DeviceStatus.ACTIVE);
        List<String> playerIds = devices.stream().filter(d -> !d.getId().equals(device.getId()))
                .map(Device::getOneSignalToken)
                .collect(Collectors.toList());

        PushNotificationRequestDTO notificationRequestDTO = PushNotificationRequestDTO.builder()
                .userId(user.getId().toString())
                .notificationDefinitionId(PropertyConstants.NOTIFICATION_DEFINITION_ID)
                .playerIds(playerIds)
                .data(data)
                .build();
        log.info("sending push notification to user's active devices ...");
        notificationRepository.sendPushNotification(notificationRequestDTO);
        log.info("Successfully sended push notification to user's active devices");

    }

}
