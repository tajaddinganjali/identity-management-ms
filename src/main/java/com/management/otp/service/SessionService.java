package com.management.otp.service;

import com.management.model.Device;
import com.management.model.Session;
import com.management.model.User;

public interface SessionService {

    Session create(User user, String deviceCode, String consumerId);

    Session setAuthorizationToken(String id, String authorizationToken);

    String getSessionIdFromToken(String token);

    String getUserIdFromToken(String token);

    String getDeviceIdFromToken(String token);

    String getConsumerIdFromToken(String token);

    Session activateSessionForWeb(String id, String authorizationCode);

    Session activateSessionForMobile(User user, Device device, String id,
                                     String authorizationCode,
                                     String deviceId);

    Session findById(String id);

    Session findActiveSessionById(String id);

    void saveUserToCache(User user);

    Session save(Session session);

    Session refreshSessionForWeb(Session session);

    Session refreshSessionForMobile(Session session, String deviceId);

    void deactivateActiveSesssionsByUserAndDevice(User user, Device device);

}
