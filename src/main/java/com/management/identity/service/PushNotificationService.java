package com.management.identity.service;

import com.management.model.Device;
import com.management.model.User;

public interface PushNotificationService {

    void sendPushNotification(User user, Device device);

}
