package com.management.repository;

import com.management.config.NotificationFeignConfig;
import com.management.identity.dto.PushNotificationRequestDTO;
import com.management.logging.CustomFeignLogging;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "notification", url = "${notification.url}",
        configuration = {NotificationFeignConfig.class, CustomFeignLogging.class})
public interface NotificationRepository {

    @PostMapping("/notifications")
    void sendPushNotification(@RequestBody PushNotificationRequestDTO notificationRequestDTO);

}
