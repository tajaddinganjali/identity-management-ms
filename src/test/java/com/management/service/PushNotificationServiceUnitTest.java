package com.management.service;

import com.management.identity.service.impl.PushNotificationServiceImpl;
import com.management.model.Device;
import com.management.model.User;
import com.management.repository.DeviceRepository;
import com.management.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PushNotificationServiceUnitTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @InjectMocks
    private PushNotificationServiceImpl pushNotificationService;

    @Test
    void sendPushNotification() {
        Device device = Device.builder()
                .id(UUID.randomUUID())
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Mockito.when(deviceRepository.findDevicesByUserAndDeviceStatus(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(device));
        Mockito.doNothing().when(notificationRepository).sendPushNotification(Mockito.any());
        Assertions.assertDoesNotThrow(() -> pushNotificationService.sendPushNotification(user, device));
    }

}
