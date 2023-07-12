package com.management.service;

import com.management.exception.BadRequestException;
import com.management.model.Device;
import com.management.model.Session;
import com.management.model.User;
import com.management.model.enums.UserStatus;
import com.management.otp.service.CacheService;
import com.management.otp.service.impl.SessionServiceImpl;
import com.management.repository.SessionRepository;
import com.management.util.IJwtUtility;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceUnitTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private IJwtUtility jwtUtility;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void create() {
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.create(User.builder().build(), "code", "id"));
    }

    @Test
    void getSessionIdFromToken() {
        Mockito.when(jwtUtility.getSessionIdFromToken(Mockito.anyString())).thenReturn("token");
        Assertions.assertDoesNotThrow(() -> sessionService.getSessionIdFromToken("token"));
    }

    @Test
    void getUserIdFromToken() {
        Mockito.when(jwtUtility.getUserIdFromToken(Mockito.anyString())).thenReturn("token");
        Assertions.assertDoesNotThrow(() -> sessionService.getUserIdFromToken("token"));
    }

    @Test
    void getDeviceIdFromToken() {
        Mockito.when(jwtUtility.getDeviceIdFromToken(Mockito.anyString())).thenReturn("token");
        Assertions.assertDoesNotThrow(() -> sessionService.getDeviceIdFromToken("token"));
    }

    @Test
    void getConsumerIdFromToken() {
        Mockito.when(jwtUtility.getConsumerIdFromToken(Mockito.anyString())).thenReturn("token");
        Assertions.assertDoesNotThrow(() -> sessionService.getConsumerIdFromToken("token"));
    }

    @Test
    void setAuthorizationToken() {
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(Session.builder().build()));
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.setAuthorizationToken("9092a293-caaf-4c26-94ea-679a9ffef7a0", "token"));
    }

    @Test
    void saveUserToCache() {
        Mockito.doNothing().when(cacheService).addUserToCache(Mockito.any());
        Assertions.assertDoesNotThrow(() -> sessionService.saveUserToCache(User.builder().build()));
    }

    @Test
    void activateSessionForWeb() {
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(getSession()));
        Mockito.when(jwtUtility.createAccessTokenFoWeb(Mockito.any())).thenReturn("token");
        Mockito.when(jwtUtility.createRefreshTokenForWeb(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn("token");
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.activateSessionForWeb("9092a293-caaf-4c26-94ea-679a9ffef7a0", "code"));
    }

    @Test
    void activateSessionForWeb_blocked() {
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .consumer("consumer")
                .user(User.builder().id(UUID.randomUUID()).status(UserStatus.BLOCKED).build())
                .build();
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(session));
        Assertions.assertThrows(BadRequestException.class, () -> sessionService.activateSessionForWeb("9092a293-caaf-4c26-94ea-679a9ffef7a0", "code"));
    }

    @Test
    void activateSessionForMobile() {
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(getSession()));
        Mockito.doNothing().when(sessionRepository).deactivateActiveSessionsByUserAndDevice(Mockito.any(), Mockito.any());
        Mockito.when(jwtUtility.createAccessTokenForMobile(Mockito.any())).thenReturn("token");
        Mockito.when(jwtUtility.createRefreshTokenForMobile(Mockito.any())).thenReturn("token");
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.activateSessionForMobile(
                getUser(), Device.builder().build(), "9092a293-caaf-4c26-94ea-679a9ffef7a0", "code", "id"));
    }

    @Test
    void activateSessionForMobile_blocked() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .status(UserStatus.BLOCKED)
                .build();
        Device device = Device.builder().build();
        Session session = Session.builder()
                .id(UUID.randomUUID())
                .consumer("consumer")
                .user(user)
                .build();
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(session));
        Assertions.assertThrows(BadRequestException.class, () -> sessionService.activateSessionForMobile(
                user, device, "9092a293-caaf-4c26-94ea-679a9ffef7a0", "code", "id"));
    }

    @Test
    void findById_exception() {
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(BadRequestException.class, () -> sessionService.findById("9092a293-caaf-4c26-94ea-679a9ffef7a0"));
    }

    @Test
    void findById() {
        Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(Session.builder().build()));
        Assertions.assertDoesNotThrow(() -> sessionService.findById("9092a293-caaf-4c26-94ea-679a9ffef7a0"));
    }

    @Test
    void findActiveSessionById() {
        Mockito.when(sessionRepository.findByIdAndStatus(Mockito.any(), Mockito.any())).thenReturn(Optional.of(Session.builder().build()));
        Assertions.assertDoesNotThrow(() -> sessionService.findActiveSessionById("9092a293-caaf-4c26-94ea-679a9ffef7a0"));
    }

    @Test
    void save() {
        Session session = Session.builder().build();
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(session);
        Assertions.assertDoesNotThrow(() -> sessionService.save(session));
    }

    @Test
    void refreshSessionForWeb() {
        Mockito.when(jwtUtility.createAccessTokenFoWeb(Mockito.any())).thenReturn("token");
        Mockito.when(jwtUtility.createRefreshTokenForWeb(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn("token");
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.refreshSessionForWeb(getSession()));
    }

    @Test
    void refreshSessionForMobile() {
        Mockito.when(jwtUtility.createAccessTokenForMobile(Mockito.any())).thenReturn("token");
        Mockito.when(jwtUtility.createRefreshTokenForMobile(Mockito.any())).thenReturn("token");
        Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(Session.builder().build());
        Assertions.assertDoesNotThrow(() -> sessionService.refreshSessionForMobile(getSession(), "id"));
    }

    @Test
    void deactivateActiveSesssionsByUserAndDevice() {
        Mockito.doNothing().when(sessionRepository).deactivateActiveSessionsByUserAndDevice(Mockito.any(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> sessionService.deactivateActiveSesssionsByUserAndDevice(User.builder().build(), Device.builder().build()));
    }

    private User getUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Session getSession() {
        return Session.builder()
                .id(UUID.randomUUID())
                .consumer("consumer")
                .user(getUser())
                .build();
    }

}
