package com.management.event.listener;

import com.management.event.UserRegistrationCompletedEvent;
import com.management.identity.service.PushNotificationService;
import com.management.util.constant.ConsumerConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewDevicePushNotificationEventListener {

    private final PushNotificationService pushNotificationService;

    @Async
    @EventListener
    public void handleNewDeviceRegistrationCompletedEventPushNotification(UserRegistrationCompletedEvent event) {
        log.info("handled new device registration completed event");
        if (event.getConsumerId().equals(ConsumerConstant.MOBILE)) {
            pushNotificationService.sendPushNotification(event.getUser(), event.getDevice());
        }
    }

}
