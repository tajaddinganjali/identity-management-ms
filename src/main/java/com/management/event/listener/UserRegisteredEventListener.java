package com.management.event.listener;

import az.ibar.eventhub.dto.TopicKeyDTO;
import az.ibar.eventhub.service.EventPublisherService;
import com.management.event.UserRegistrationCompletedEvent;
import com.management.event.dto.CifDTO;
import com.management.event.dto.CurrentDeviceDTO;
import com.management.event.dto.DeviceDTO;
import com.management.event.dto.PhoneDTO;
import com.management.event.dto.RegistrationDTO;
import com.management.event.dto.UserDTO;
import com.management.event.dto.UserRegisteredDTO;
import com.management.event.dto.UserRegisteredEventDTO;
import com.management.model.Device;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserRegistration;
import com.management.otp.service.CacheService;
import com.management.register.dto.internal.ReferralInfoDTO;
import com.management.register.enums.KafkaEventType;
import com.management.register.service.ReferralService;
import com.management.util.KafkaHeaderProviderUtil;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final EventPublisherService eventPublisherService;
    private final ReferralService referralService;
    private final CacheService cacheService;

    @Async
    @EventListener
    public void handleUserRegisteredEventForKafka(UserRegistrationCompletedEvent event) {
        log.info("handled user registered event for kafka promotion, saving user to db");
        String eventId = UUID.randomUUID().toString();
        TopicKeyDTO topicKeyDTO = TopicKeyDTO.builder()
                .key(event.getUser().getId().toString())
                .topic("events")
                .build();

        UserRegisteredDTO userRegisteredDTO = UserRegisteredDTO.builder()
                .id(eventId)
                .user(setAndGetUserDTO(event.getUser()))
                .device(setAndGetDevice(event.getDevice()))
                .newRegister(event.isFirstRegistration())
                .build();

        String deviceId = "";
        if (Objects.nonNull(event.getDevice())) {
            deviceId = event.getDevice().getId().toString();
        }

        KafkaEventType kafkaEventType = (KafkaEventType) cacheService.getValue(
                DigestUtils.sha256Hex(event.getUser().getId() + event.getConsumerId() + deviceId));

        UserRegisteredEventDTO userRegisteredEventDTO = UserRegisteredEventDTO.builder()
                .header(KafkaHeaderProviderUtil
                        .getHeader(eventId, kafkaEventType.getEventType(), event.getConsumerId()))
                .body(userRegisteredDTO)
                .build();
        eventPublisherService.publish(userRegisteredEventDTO, Collections.singletonList(topicKeyDTO));
    }

    private UserDTO setAndGetUserDTO(User user) {
        ReferralInfoDTO referralFromCache = (ReferralInfoDTO) cacheService.getValue(user.getId().toString());

        ReferralInfoDTO referralDTO = null;

        if (referralFromCache != null) {
            referralService.updateReferralStatus(user.getId());
            referralDTO = ReferralInfoDTO.builder()
                    .referralType(referralFromCache.getReferralType())
                    .referredBy(referralFromCache.getReferredBy())
                    .build();
        }

        return UserDTO.builder()
                .id(user.getId().toString())
                .pin(user.getPin())
                .cardNumber(user.getCardNumber())
                .phone(user.getPhone())
                .name(user.getName())
                .surname(user.getSurname())
                .cif(user.getCif())
                .cifs(setAndGetCifList(user.getCifs()))
                .registrations(setAndGetRegistrationList(user.getRegistrations()))
                .gender(user.getGender())
                .maritalStatus(user.getMaritalStatus())
                .resident(user.getResident())
                .registrationDate(user.getUpdatedAt())
                .birthDate(user.getDateOfBirth())
                .age(user.getAge())
                .profileType(user.getProfileType())
                .language(user.getLanguage())
                .referral(referralDTO)
                .build();
    }

    private List<CifDTO> setAndGetCifList(List<UserCif> userCifs) {
        return userCifs.stream()
                .map(cif -> CifDTO.builder()
                        .cif(cif.getCif())
                        .phones(cif.getPhones().stream()
                                .map(phone -> PhoneDTO.builder()
                                        .phone(phone.getPhone())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<RegistrationDTO> setAndGetRegistrationList(List<UserRegistration> userRegistrations) {
        return userRegistrations.stream()
                .map(userRegistration -> RegistrationDTO.builder()
                        .id(userRegistration.getId().toString())
                        .phone(userRegistration.getPhone())
                        .status(userRegistration.getStatus())
                        .consumer(userRegistration.getConsumer())
                        .channel(userRegistration.getChannel())
                        .build())
                .collect(Collectors.toList());
    }

    private DeviceDTO setAndGetDevice(Device device) {
        if (Objects.isNull(device)) {
            return null;
        }
        CurrentDeviceDTO currentDeviceDTO = CurrentDeviceDTO.builder()
                .id(device.getId().toString())
                .deviceModel(device.getDeviceModel())
                .deviceOs(device.getDeviceOs())
                .deviceOsVersion(device.getDeviceOsVersion())
                .appVersion(device.getAppVersion())
                .oneSignalToken(device.getOneSignalToken())
                .jailyBroken(device.isJailyBroken())
                .deviceCode(device.getDeviceCode())
                .deviceNo(device.getDeviceNo())
                .pushEnabled(device.isPushEnabled())
                .build();

        return DeviceDTO.builder()
                .currentDevice(currentDeviceDTO)
                .build();
    }

}
