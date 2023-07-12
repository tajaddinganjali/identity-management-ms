package com.management.service;

import com.management.event.publisher.CustomEventPublisher;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechnicalException;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.UserAudService;
import com.management.identity.service.impl.UserServiceImpl;
import com.management.model.Channel;
import com.management.model.CifPhone;
import com.management.model.Consumer;
import com.management.model.Device;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserCifAud;
import com.management.model.UserRegistration;
import com.management.model.enums.UserStatus;
import com.management.otp.dto.UserCifPhoneMapDTO;
import com.management.otp.service.CacheService;
import com.management.repository.DeviceRepository;
import com.management.repository.UserCifAudRepository;
import com.management.repository.UserCifPhoneRepository;
import com.management.repository.UserCifRepository;
import com.management.repository.UserRepository;
import com.management.util.PasswordUtil;
import com.management.util.constant.ConsumerConstant;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    public static final String USER_ID = "9092a293-caaf-4c26-94ea-679a9ffef7a0";
    public static final String DEVICE_ID = "5a588047-a15a-45af-89af-885e6a5b8514";
    public static final String PIN = "63RA5BV";
    public static final String PASSCODE = "1357";

    @Mock
    private UserAudService userAudService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private CustomEventPublisher customEventPublisher;
    @Mock
    private PasswordUtil passwordUtil;
    @Mock
    private CacheService cacheService;
    @Mock
    private UserCifRepository userCifRepository;
    @Mock
    private UserCifPhoneRepository userCifPhoneRepository;
    @Mock
    private UserCifAudRepository userCifAudRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(User.builder().build());
        Assertions.assertDoesNotThrow(() -> userService.save(getUser()));
    }

    @Test
    void findByPin() {
        Mockito.when(userRepository.findByPin(Mockito.anyString())).thenReturn(Optional.of(getUser()));
        Assertions.assertDoesNotThrow(() -> userService.findByPin("pin"));
    }

    @Test
    void findUserById() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(User.builder().build()));
        Assertions.assertDoesNotThrow(() -> userService.findUserById(USER_ID));
    }

    @Test
    void findActiveUserById() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getUser()));
        Assertions.assertDoesNotThrow(() -> userService.findActiveUserById(USER_ID));
    }

    @Test
    void findActiveUserById_exception() {
        User user = User.builder()
                .status(UserStatus.BLOCKED)
                .build();
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Assertions.assertThrows(BadRequestException.class,
                () -> userService.findActiveUserById(USER_ID));
    }

    @Test
    void findActiveUserById_exceptionn() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(BadRequestException.class,
                () -> userService.findActiveUserById(USER_ID));
    }

    @Test
    void createAuditAndUpdateUser() {
        Mockito.doNothing().when(userAudService).save(Mockito.any());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(User.builder().build());
        Assertions.assertDoesNotThrow(() -> userService.createAuditAndUpdateUser(
                User.builder().id(UUID.fromString("5289012c-95d0-418f-843a-b5a4d99deb34")).build(),
                User.builder().id(UUID.fromString("5289012c-95d0-418f-843a-b5a4d99deb34")).build()));
    }

    @Test
    void createAuditAndUpdateUser_throws() {
        Exception exception = Assertions.assertThrows(GeneralTechnicalException.class,
                () -> userService.createAuditAndUpdateUser(getUser(), getUser()));
        Assertions.assertTrue(exception.getMessage().contains("duplicate_user"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "customer_id"})
    void updateUser(String userId) {
        Consumer consumer = Consumer.builder()
                .id(userId)
                .channel(Channel.MOBILE)
                .build();
        Assertions.assertDoesNotThrow(() -> userService.updateUser(getUser(), getUser(), consumer));
    }

    @Test
    void updateUser_blockedUser() {
        User user = User.builder()
                .status(UserStatus.BLOCKED)
                .build();
        Consumer consumer = Consumer.builder()
                .id("id")
                .channel(Channel.MOBILE)
                .build();
        Assertions.assertThrows(BadRequestException.class, () -> userService.updateUser(user, user, consumer));
    }

    @Test
    void setUserPassCode() throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = User.builder()
                .registrations(List.of(UserRegistration.builder()
                        .consumer(ConsumerConstant.MOBILE)
                        .build()))
                .build();
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(deviceRepository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(Device.builder().build()));
        Mockito.doNothing().when(userAudService).save(Mockito.any());
        Mockito.when(passwordUtil.hash(Mockito.anyString(), Mockito.any())).thenReturn("pass");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(getUser());
        Mockito.doNothing().when(customEventPublisher).publishUserRegistrationCompletedEvent(Mockito.any());
        Assertions.assertDoesNotThrow(
                () -> userService.setUserPassCode(USER_ID, PASSCODE, ConsumerConstant.MOBILE, "lang", DEVICE_ID,
                        false));
    }


    @Test
    void setUserPassCode_exception() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getUser()));
        Mockito.when(deviceRepository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(Device.builder().build()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.setUserPassCode(USER_ID, PASSCODE, "id", "lang", DEVICE_ID, false));
    }


    @ParameterizedTest
    @EnumSource(value = ProfileType.class, names = {"CUSTOMER", "POTENTIAL_CUSTOMER"})
    void saveUserCifPhonesToDatabase(ProfileType profileType) {
        Map<String, List<String>> userPhoneCifs = new HashMap<>();
        UserCifPhoneMapDTO userCifPhoneMapDTO = UserCifPhoneMapDTO.builder()
                .userPhoneCifs(userPhoneCifs)
                .isPreviousCifsMustBeDeleted(true)
                .build();
        Mockito.when(cacheService.getUserPhoneCifs(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(userCifPhoneMapDTO);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(getUser()));
        auditAndDeletePreviousUserCifs();
        saveCifPhonesToDb();
        Assertions.assertDoesNotThrow(() -> userService.saveUserCifPhonesToDatabase(
                "17954edc-efd6-11ec-8ea0-0242ac120002", "token", profileType));
    }

    @Test
    void saveUserCifPhonesToDatabase_fail() {
        Mockito.when(cacheService.getUserPhoneCifs(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Assertions.assertThrows(BadRequestException.class, () -> userService.saveUserCifPhonesToDatabase(
                "17954edc-efd6-11ec-8ea0-0242ac120002", "token", ProfileType.CUSTOMER));
    }

    @Test
    void saveUserCifPhonesToCache() {
        Map<String, List<String>> userPhoneCifs = new HashMap<>();
        Mockito.doNothing().when(cacheService)
                .saveUserPhoneCifs(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        Assertions.assertDoesNotThrow(() -> userService.saveUserCifPhonesToCache(
                "userId", "token", userPhoneCifs, true));
    }

    @Test
    void saveCifPhonesToDb() {
        Map<String, List<String>> userPhoneCifs = new HashMap<>();
        userPhoneCifs.put("phone", List.of("phones"));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(getUser());
        Mockito.when(userCifRepository.findByUserAndCif(Mockito.any(), Mockito.anyString()))
                .thenReturn(Optional.empty());
        Assertions.assertDoesNotThrow(() -> userService.saveCifPhonesToDb(userPhoneCifs, getUser()));
    }

    @Test
    void auditAndDeletePreviousUserCifs() {
        UserCif userCif = UserCif.builder()
                .user(getUser())
                .version(1L)
                .createdAt(OffsetDateTime.now())
                .cif("cif")
                .build();
        Mockito.when(userCifRepository.findByUser(Mockito.any())).thenReturn(List.of(userCif));
        Mockito.when(userCifPhoneRepository.findByCifId(Mockito.any()))
                .thenReturn(List.of(CifPhone.builder().id(UUID.randomUUID()).build()));
        Mockito.doNothing().when(userCifPhoneRepository).deleteById(Mockito.any());
        Mockito.doNothing().when(userCifRepository).deleteById(Mockito.any());
        Mockito.when(userCifAudRepository.save(Mockito.any())).thenReturn(UserCifAud.builder().build());
        Assertions.assertDoesNotThrow(() -> userService.auditAndDeletePreviousUserCifs(getUser()));
    }

    private User getUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .age(1)
                .cardNumber("card")
                .cif("cif")
                .profileType(ProfileType.CUSTOMER)
                .gender("gender")
                .identityIssuingDate("date")
                .identityMaturityDate("date")
                .identityNumber("number")
                .identityType("type")
                .identityIssuingDate("type")
                .maritalStatus("status")
                .dateOfBirth(Date.valueOf(LocalDate.now()))
                .name("name")
                .passcode("code")
                .phone("phone")
                .pin("pin")
                .status(UserStatus.ACTIVE)
                .version(1L)
                .surname("surname")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .resident(true)
                .cifs(List.of(UserCif.builder().cif("cif").build()))
                .registrations(List.of(UserRegistration.builder().consumer("id").build()))
                .build();
    }

}
