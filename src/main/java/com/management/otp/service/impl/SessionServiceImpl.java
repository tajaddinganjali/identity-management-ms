package com.management.otp.service.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.model.Device;
import com.management.model.Session;
import com.management.model.User;
import com.management.model.enums.SessionStatus;
import com.management.model.enums.UserStatus;
import com.management.otp.service.CacheService;
import com.management.otp.service.SessionService;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.repository.SessionRepository;
import com.management.util.IJwtUtility;
import com.management.util.dto.JwtUtilityDTO;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final IJwtUtility jwtUtility;
    private final CacheService cacheService;


    @Override
    public Session create(User user, String deviceCode, String consumerId) {

        Session session = Session.builder()
                .user(user)
                .consumer(consumerId)
                .status(SessionStatus.INACTIVE)
                .deviceCode(deviceCode)
                .build();

        return sessionRepository.save(session);
    }

    @Override
    public String getSessionIdFromToken(String token) {
        return jwtUtility.getSessionIdFromToken(token);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtUtility.getUserIdFromToken(token);
    }

    @Override
    public String getDeviceIdFromToken(String token) {
        return jwtUtility.getDeviceIdFromToken(token);
    }

    @Override
    public String getConsumerIdFromToken(String token) {
        return jwtUtility.getConsumerIdFromToken(token);
    }

    @Override
    public Session setAuthorizationToken(String id, String authorizationToken) {

        Session session = findById(id);

        session.setAuthorizationToken(authorizationToken);

        return sessionRepository.save(session);
    }

    @Override
    public void saveUserToCache(User user) {
        cacheService.addUserToCache(user);
    }


    @Override
    public Session activateSessionForWeb(String id, String authorizationCode) {

        Session session = findById(id);

        if (session.getUser().getStatus().equals(UserStatus.BLOCKED)) {
            throw new BadRequestException(BadRequestErrorEnum.USER_WITH_BLOCKED_STATUS);
        }

        setTokensForWeb(session);

        session.setStatus(SessionStatus.ACTIVE);
        session.setAuthorizationCode(authorizationCode);

        return sessionRepository.save(session);
    }

    @Override
    public Session activateSessionForMobile(User user, Device device, String id,
                                            String authorizationCode,
                                            String deviceId) {
        Session session = findById(id);

        if (session.getUser().getStatus().equals(UserStatus.BLOCKED)) {
            throw new BadRequestException(BadRequestErrorEnum.USER_WITH_BLOCKED_STATUS);
        }

        sessionRepository.deactivateActiveSessionsByUserAndDevice(user, device);

        setTokensForMobile(session, deviceId);

        session.setStatus(SessionStatus.ACTIVE);
        session.setAuthorizationCode(authorizationCode);
        session.setDevice(device);

        return sessionRepository.save(session);
    }

    private void setTokensForWeb(Session session) {
        setAccessTokenForWeb(session);
        setRefreshTokenForWeb(session);
    }

    private void setTokensForMobile(Session session, String deviceId) {
        setAccessTokenForMobile(session, deviceId);
        setRefreshTokenForMobile(session, deviceId);
    }

    private void setAccessTokenForWeb(Session session) {
        String accessToken = jwtUtility
                .createAccessTokenFoWeb(JwtUtilityDTO.builder()
                        .userId(session.getUser().getId().toString())
                        .sessionId(session.getId().toString())
                        .consumerTypeEnum(ConsumerTypeEnum.findByNameOrElseIbaMobile(session.getConsumer()))
                        .build());
        session.setAccessToken(accessToken);
    }

    private void setRefreshTokenForWeb(Session session) {
        String refreshToken = jwtUtility
                .createRefreshTokenForWeb(session.getUser().getId().toString(), session.getId().toString(),
                        ConsumerTypeEnum.findByNameOrElseIbaMobile(session.getConsumer()));
        session.setRefreshToken(refreshToken);
    }

    private void setAccessTokenForMobile(Session session, String deviceId) {
        String accessToken = jwtUtility
                .createAccessTokenForMobile(JwtUtilityDTO.builder()
                        .userId(session.getUser().getId().toString())
                        .sessionId(session.getId().toString())
                        .consumerTypeEnum(ConsumerTypeEnum.findByNameOrElseIbaMobile(session.getConsumer()))
                        .deviceId(deviceId)
                        .build());
        session.setAccessToken(accessToken);
    }

    private void setRefreshTokenForMobile(Session session, String deviceId) {
        String refreshToken =
                jwtUtility.createRefreshTokenForMobile(JwtUtilityDTO.builder()
                        .userId(session.getUser().getId().toString())
                        .sessionId(session.getId().toString())
                        .consumerTypeEnum(ConsumerTypeEnum.findByNameOrElseIbaMobile(session.getConsumer()))
                        .deviceId(deviceId)
                        .build());
        session.setRefreshToken(refreshToken);
    }

    @Override
    public Session findById(String id) {
        UUID sessionId = UUID.fromString(id);
        return sessionRepository.findById(sessionId).orElseThrow(
                () -> new BadRequestException(BadRequestErrorEnum.INVALID_SESSION));
    }

    @Override
    public Session findActiveSessionById(String id) {
        UUID sessionId = UUID.fromString(id);
        return sessionRepository.findByIdAndStatus(sessionId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException(BadRequestErrorEnum.INVALID_SESSION));
    }

    @Override
    public Session save(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public Session refreshSessionForWeb(Session session) {
        setRefreshTokenForWeb(session);
        setAccessTokenForWeb(session);
        return save(session);
    }

    @Override
    public Session refreshSessionForMobile(Session session, String deviceId) {
        setTokensForMobile(session, deviceId);
        return save(session);
    }

    @Override
    public void deactivateActiveSesssionsByUserAndDevice(User user, Device device) {
        sessionRepository.deactivateActiveSessionsByUserAndDevice(user, device);
    }

}

