package com.management.identity.service.impl;

import com.management.event.UserRegistrationCompletedEvent;
import com.management.event.publisher.CustomEventPublisher;
import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.exception.NotFoundErrorEnum;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.UserAudService;
import com.management.identity.service.UserService;
import com.management.model.CifPhone;
import com.management.model.Consumer;
import com.management.model.Device;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserCifAud;
import com.management.model.UserRegistration;
import com.management.model.enums.RegistrationStatus;
import com.management.model.enums.UserStatus;
import com.management.otp.dto.UserCifPhoneMapDTO;
import com.management.otp.service.CacheService;
import com.management.repository.DeviceRepository;
import com.management.repository.UserCifAudRepository;
import com.management.repository.UserCifPhoneRepository;
import com.management.repository.UserCifRepository;
import com.management.repository.UserRepository;
import com.management.util.PasswordUtil;
import com.management.util.UserUtil;
import com.management.util.constant.ConsumerConstant;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CacheService cacheService;
    private final UserAudService userAudService;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordUtil passwordUtil;
    private final CustomEventPublisher eventPublisher;
    private final UserCifRepository userCifRepository;
    private final UserCifPhoneRepository userCifPhoneRepository;
    private final UserCifAudRepository userCifAudRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User userFromIamDb, User userFromProvider, Consumer consumer) {
        boolean isRegistrationFound = false;

        if (userFromIamDb.getStatus().equals(UserStatus.BLOCKED)) {
            throw new BadRequestException(BadRequestErrorEnum.USER_WITH_BLOCKED_STATUS);
        }
        String phone = userFromIamDb.getPhone();
        User updatedUser = UserUtil.mergeUser(userFromIamDb, userFromProvider);
        if (!consumer.getId().equals(ConsumerConstant.MOBILE)) {
            updatedUser.setPhone(phone);
        }
        for (UserRegistration registration : updatedUser.getRegistrations()) {
            if (registration.getConsumer().equals(consumer.getId())) {
                isRegistrationFound = true;
                registration.setPhone(userFromProvider.getPhone());
                registration.setChannel(consumer.getChannel());
            } else if (registration.getConsumer().equals(ConsumerConstant.WEB)) {
                registration.setPhone(userFromProvider.getPhone());
            }
        }
        if (!isRegistrationFound) {
            UserRegistration registration = UserRegistration.builder()
                    .consumer(consumer.getId())
                    .phone(userFromProvider.getPhone())
                    .status(RegistrationStatus.REGISTERING)
                    .channel(consumer.getChannel())
                    .user(updatedUser)
                    .build();

            updatedUser.setRegistrations(Collections.singletonList(registration));
        }
        return createAuditAndUpdateUser(userFromIamDb, updatedUser);
    }

    @Override
    public Optional<User> findByPin(String pin) {
        return userRepository.findByPin(pin);
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId));
    }

    @Override
    public User findActiveUserById(String userId) {
        Optional<User> optionalUser = findUserById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new BadRequestException(BadRequestErrorEnum.USER_WITH_BLOCKED_STATUS);
            }
        } else {
            throw new BadRequestException(BadRequestErrorEnum.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public void setUserPassCode(String userId, String passcode, String consumerId, String lang, String deviceId,
                                boolean agreementSigned)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        User user = findUserById(userId).orElseThrow(() -> new BadRequestException(NotFoundErrorEnum.USER_NOT_FOUND));
        Device device = null;
        if (!StringUtils.isEmpty(deviceId)) {
            Optional<Device> deviceOptional = deviceRepository.findById(UUID.fromString(deviceId));
            if (deviceOptional.isPresent()) {
                device = deviceOptional.get();
            }
        }
        boolean isFirstRegistration = false;
        isFirstRegistration = isFirstRegistration(passcode, consumerId, user, isFirstRegistration);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        UserRegistrationCompletedEvent event = UserRegistrationCompletedEvent.builder()
                .user(user)
                .consumerId(consumerId)
                .isFirstRegistration(isFirstRegistration)
                .device(device)
                .build();
        eventPublisher.publishUserRegistrationCompletedEvent(event);
    }

    private boolean isFirstRegistration(String passcode, String consumerId, User user, boolean isFirstRegistration)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hashedPassword;
        if (consumerId.equalsIgnoreCase(ConsumerConstant.MOBILE)) {
            hashedPassword = passwordUtil.hash(passcode, null);
            for (UserRegistration userRegistration : user.getRegistrations()) {
                if (userRegistration.getConsumer().equals(consumerId)) {
                    if (Objects.isNull(userRegistration.getPassword())) {
                        isFirstRegistration = true;
                    }
                    userRegistration.setPassword(hashedPassword);
                    userRegistration.setStatus(RegistrationStatus.ACTIVE);
                }
            }
            userAudService.save(user);
            user.setPasscode(hashedPassword);
        } else {
            throw new IllegalArgumentException("Invalid consumer id");

        }
        return isFirstRegistration;
    }

    @Override
    public void saveUserCifPhonesToCache(String userId, String verificationToken,
                                         Map<String, List<String>> userPhoneCifs, boolean isPreviousCifsMustBeDeleted) {
        UserCifPhoneMapDTO userCifPhoneMapDTO = UserCifPhoneMapDTO.builder()
                .userPhoneCifs(userPhoneCifs)
                .isPreviousCifsMustBeDeleted(isPreviousCifsMustBeDeleted)
                .build();
        cacheService.saveUserPhoneCifs(userId, verificationToken, userCifPhoneMapDTO);
    }

    @Override
    public void saveUserCifPhonesToDatabase(String userId, String verificationToken, ProfileType profileType) {

        UserCifPhoneMapDTO userPhoneCifsMap = cacheService.getUserPhoneCifs(userId, verificationToken);
        if (profileType == ProfileType.CUSTOMER) {
            if (Objects.isNull(userPhoneCifsMap)) {
                throw new BadRequestException(BadRequestErrorEnum.PROCESS_TIME_EXPIRED);
            }
        } else {
            userPhoneCifsMap = UserCifPhoneMapDTO.builder()
                    .userPhoneCifs(Map.of("mobileNo",
                            List.of("CustomerNo")))
                    .build();
        }
        Map<String, List<String>> userPhoneCifs = userPhoneCifsMap.getUserPhoneCifs();

        boolean isPreviousCifsMustBeDeleted = userPhoneCifsMap.isPreviousCifsMustBeDeleted();

        Optional<User> userOptional = userRepository.findById(UUID.fromString(userId));
        User user = userOptional.orElseThrow(() -> new BadRequestException(BadRequestErrorEnum.USER_NOT_FOUND));

        if (isPreviousCifsMustBeDeleted) {
            log.info("auditing and deleting previous user cifs...");
            auditAndDeletePreviousUserCifs(user);
            user.setCifs(null);
        }
        saveCifPhonesToDb(userPhoneCifs, user);
        cacheService.deleteUserFromCache(userId);
    }

    @Override
    public void saveCifPhonesToDb(Map<String, List<String>> userPhoneCifs, User user) {
        if (Objects.nonNull(userPhoneCifs)) {
            for (Map.Entry<String, List<String>> userCifPhonesEntry : userPhoneCifs.entrySet()) {
                String phone = userCifPhonesEntry.getKey();
                List<String> cifList = userCifPhonesEntry.getValue();

                String defaultCif = cifList.get(0);
                user.setCif(defaultCif);
                userRepository.save(user);
                saveOrUpdateUserCif(user, phone, cifList);
            }
        }
    }

    private void saveOrUpdateUserCif(User user, String phone, List<String> cifList) {
        for (String cif : cifList) {
            Optional<UserCif> optionalUserCif = userCifRepository.findByUserAndCif(user, cif);

            if (optionalUserCif.isPresent()) {
                UserCif userCif = optionalUserCif.get();
                Optional<CifPhone> optionalCifPhone =
                        userCif.getPhones().stream().filter(p -> p.getPhone().equals(phone)).findAny();
                if (optionalCifPhone.isEmpty()) {
                    CifPhone cifPhone = CifPhone.builder()
                            .phone(phone)
                            .cif(userCif)
                            .build();
                    userCif.getPhones().add(cifPhone);
                    userCifRepository.save(userCif);
                }
            } else {
                UserCif userCif = UserCif.builder()
                        .cif(cif)
                        .user(user)
                        .build();
                CifPhone cifPhone = CifPhone.builder()
                        .cif(userCif)
                        .phone(phone)
                        .build();
                userCif.setPhones(Collections.singletonList(cifPhone));
                userCifRepository.save(userCif);
            }
        }
    }

    public User createAuditAndUpdateUser(User currentUser, User updatedUser) {
        if (!currentUser.getId().toString().equalsIgnoreCase(updatedUser.getId().toString())) {
            log.error(String.format("duplicate_user PIN: %s, currUID: %s, updtUID: %s", updatedUser.getPin(),
                    currentUser.getId(), updatedUser.getId()));
            throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "duplicate_user");
        }
        userAudService.save(currentUser);
        return userRepository.save(updatedUser);
    }

    @Override
    public void auditAndDeletePreviousUserCifs(User user) {
        List<UserCif> userCifs = userCifRepository.findByUser(user);
        userCifs.forEach(this::deleteUserCifs);
    }

    private void deleteUserCifs(UserCif userCif) {
        List<CifPhone> userCifPhones =
                userCifPhoneRepository.findByCifId(userCif.getId());

        userCifPhones.forEach(p -> userCifPhoneRepository.deleteById(p.getId()));
        userCifRepository.deleteById(userCif.getId());
        auditPreviousUserCifs(userCif);

    }

    private void auditPreviousUserCifs(UserCif userCif) {
        UserCifAud userCifAud = UserCifAud.builder()
                .createdAt(userCif.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .version(userCif.getVersion())
                .cif(userCif.getCif())
                .user(userCif.getUser())
                .build();
        userCifAudRepository.save(userCifAud);
    }

}
