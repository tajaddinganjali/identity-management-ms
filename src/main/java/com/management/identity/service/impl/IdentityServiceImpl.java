package com.management.identity.service.impl;

import static com.management.util.constant.PropertyConstants.ONE;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.exception.NotFoundErrorEnum;
import com.management.identity.dto.CheckUserRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.flex.service.FlexService;
import com.management.identity.service.ConsumerService;
import com.management.identity.service.IdentityService;
import com.management.identity.service.UserCifService;
import com.management.identity.service.UserRegistrationService;
import com.management.identity.service.UserService;
import com.management.model.Consumer;
import com.management.model.Customer;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserRegistration;
import com.management.model.enums.RegistrationStatus;
import com.management.model.enums.UserStatus;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.enums.RegisterCaseTypeEnum;
import com.management.util.RegistrationUtil;
import com.management.util.TimeUtil;
import com.management.util.UserUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityServiceImpl implements IdentityService {

    private final FlexService flexService;
    private final ConsumerService consumerService;
    private final UserService userService;
    private final UserCifService userCifService;
    private final UserRegistrationService userRegistrationService;

    @Value("${check.phone.enabled}")
    private boolean checkPhoneEnabled;

    @Override
    public RegisterCaseTypeEnum identifyUser(CheckUserRequisiteDTO checkUserRequisite) {
        RegisterCaseTypeEnum registerCaseTypeEnum;
        try {
            FlexResponseDTO<List<DataDTO>> flexResponse = flexService.getCustomer(checkUserRequisite.getPin());
            if (Objects.nonNull(flexResponse) && Objects.nonNull(flexResponse.getData())) {
                registerCaseTypeEnum = handleUserFoundFlex(checkUserRequisite, flexResponse);
            } else {
                registerCaseTypeEnum = handleUserNotFoundFlex(checkUserRequisite);
            }
        } catch (BadRequestException baseException) {
            String exceptionCode = baseException.getCode();
            boolean isUserNotFoundFlexCase =
                    exceptionCode.equalsIgnoreCase(BadRequestErrorEnum.IDENTIFIED_USER_NOT_FOUND_FLEX.getCode())
                            || baseException.getCode()
                            .equalsIgnoreCase(NotFoundErrorEnum.FLEX_USER_NOT_FOUND.getCode());
            if (isUserNotFoundFlexCase) {
                registerCaseTypeEnum = handleUserNotFoundFlex(checkUserRequisite);
            } else {
                throw baseException;
            }
        }
        return registerCaseTypeEnum;
    }

    @Override
    public FlexResponseDTO<List<DataDTO>> getFlexCustomer(String pin) {
        return flexService.getCustomer(pin);
    }

    @Override
    public User createDBUser(RegisterCacheDTO registerCache) {

        FlexResponseDTO<List<DataDTO>> flexResponse = flexService.getCustomer(registerCache.getPin());
        List<String> userFlexCifsMatchedByPhone =
                RegistrationUtil.provideUserFlexCifsMatchedByPhone(registerCache.getPhone(), flexResponse.getData());
        List<Customer> matchedCustomersByPinAndPhone =
                getMatchedCustomersByPinAndPhone(flexResponse, userFlexCifsMatchedByPhone);
        User userFormedFromFlex =
                UserUtil.convertCustomerListToUser(registerCache.getPhone(), matchedCustomersByPinAndPhone,
                        ProfileType.CUSTOMER);
        Consumer consumer = findConsumerById(registerCache.getConsumerType().getConsumerId());

        User user = getOrCreateUser(registerCache.getPin(), registerCache.getPhone(), userFlexCifsMatchedByPhone,
                userFormedFromFlex, consumer);

        saveAndAuditUserCifs(registerCache.getPhone(), userFormedFromFlex, user);

        return user;
    }

    private List<Customer> getMatchedCustomersByPinAndPhone(FlexResponseDTO<List<DataDTO>> flexResponse,
                                                            List<String> userFlexCifsMatchedByPhone) {
        List<Customer> matchedCustomersByPinAndPhone = flexResponse.getData().stream()
                .filter(c -> userFlexCifsMatchedByPhone.contains(c.getBase().getCustomerNumber())
                ).map(RegistrationUtil::convertIntegration).collect(Collectors.toList());
        if (matchedCustomersByPinAndPhone.isEmpty()) {
            throw new BadRequestException(BadRequestErrorEnum.PIN_PHONE_MISMATCH);
        }
        return matchedCustomersByPinAndPhone;
    }

    private void saveAndAuditUserCifs(String phone, User userFormedFromFlex, User user) {
        if (Objects.nonNull(userFormedFromFlex.getCifs()) && !userFormedFromFlex.getCifs().isEmpty()) {
            List<String> userCifList =
                    userFormedFromFlex.getCifs().stream().map(UserCif::getCif).collect(Collectors.toList());

            Map<String, List<String>> userPhoneCifs = new HashMap<>();
            userPhoneCifs.put(phone, userCifList);
            userService.auditAndDeletePreviousUserCifs(user);
            user.setCifs(null);
            userService.saveCifPhonesToDb(userPhoneCifs, user);
        }
    }

    private User getOrCreateUser(String pin, String phone, List<String> userFlexCifsMatchedByPhone,
                                 User userFormedFromFlex, Consumer consumer) {
        Optional<User> optionalUser = userService.findByPin(pin);
        User user;
        User userFromIamDb;
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getStatus() != UserStatus.ACTIVE && checkPhoneEnabled) {
                checkDuplicatePhone(phone, userFlexCifsMatchedByPhone, userFormedFromFlex, consumer);
                userFromIamDb = userService.findByPin(pin).orElseThrow(
                        () -> new GeneralTechnicalException(GeneralTechErrorEnum.SERVER_ERROR, "user_not_found"));
            } else {
                userFromIamDb = optionalUser.get();
            }
            user = userService.updateUser(userFromIamDb, userFormedFromFlex, consumer);
        } else {
            if (checkPhoneEnabled) {
                checkDuplicatePhone(phone, userFlexCifsMatchedByPhone, userFormedFromFlex, consumer);
            }
            user = createUser(userFormedFromFlex, consumer);
        }
        return user;
    }


    @Override
    public void checkDuplicatePhone(String phone, List<String> userFlexCifsMatchedByPhone,
                                    User userFormedFromFlex, Consumer consumer) {
        List<UserCif> userCifs = userCifService.getCifsByPhone(phone);
        if (!userCifs.isEmpty()) {
            checkCifPhoneForUserUpdate(userFormedFromFlex, consumer,
                    userFlexCifsMatchedByPhone, userCifs);
        }
    }

    @Override
    public Consumer findConsumerById(String consumerId) {
        return consumerService.findById(consumerId);
    }


    private User createUser(User userFormedFromFlex, Consumer consumer) {
        Optional<User> optionalUser = userService.findByPin(userFormedFromFlex.getPin());
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        User user;
        User currentUser = User.builder()
                .cifs(new ArrayList<>())
                .status(UserStatus.REGISTERING)
                .build();

        User creatingUser = UserUtil.mergeUser(currentUser, userFormedFromFlex);

        User createdUser = userService.save(creatingUser);

        UserRegistration registration = UserRegistration.builder()
                .consumer(consumer.getId())
                .user(createdUser)
                .phone(userFormedFromFlex.getPhone())
                .channel(consumer.getChannel())
                .status(RegistrationStatus.REGISTERING)
                .build();

        UserRegistration createdUserRegistration = userRegistrationService.save(registration);

        createdUser.setRegistrations(Collections.singletonList(createdUserRegistration));

        user = createdUser;
        return user;
    }

    private RegisterCaseTypeEnum handleUserFoundFlex(CheckUserRequisiteDTO checkUserRequisite,
                                                     FlexResponseDTO<List<DataDTO>> flexResponse) {
        if (StringUtils.isBlank(checkUserRequisite.getBirthdate())) {
            return checkExistingUserByPin(checkUserRequisite, flexResponse);
        } else {
            checkExistingUserByBirthDate(checkUserRequisite, flexResponse);
            return RegisterCaseTypeEnum.IDENTIFIED_USER_CIF_PHONE_MISMATCH;
        }
    }

    private RegisterCaseTypeEnum handleUserNotFoundFlex(CheckUserRequisiteDTO checkUserRequisite) {
        RegisterCaseTypeEnum registerCaseTypeEnum;
        if (StringUtils.isBlank(checkUserRequisite.getBirthdate())) {
            checkUserByPinAndPhone(checkUserRequisite);
        } else {
            checkUserByPinAndBirthDate(checkUserRequisite);
        }
        registerCaseTypeEnum = RegisterCaseTypeEnum.IDENTIFIED_USER_NOT_FOUND_FLEX;
        return registerCaseTypeEnum;
    }

    private void checkExistingUserByBirthDate(CheckUserRequisiteDTO checkUserRequisite,
                                              FlexResponseDTO<List<DataDTO>> flexResponse) {
        String flexDateOfBirth = TimeUtil.convertFormat(
                flexResponse.getData().stream().findFirst()
                        .map(personal -> personal.getPersonal().getDateOfBirth())
                        .orElseThrow(() -> new BadRequestException(BadRequestErrorEnum.BIRTHDATE_IS_NULL)),
                "yyyy-MM-dd", "dd.MM.yyyy");
        if (!flexDateOfBirth.equalsIgnoreCase(checkUserRequisite.getBirthdate())) {
            checkUserByPinAndBirthDate(checkUserRequisite);
        }
    }

    private RegisterCaseTypeEnum checkExistingUserByPin(CheckUserRequisiteDTO checkUserRequisite,
                                                        FlexResponseDTO<List<DataDTO>> flexResponse) {
        boolean multiCifNonResident = checkMultiCifNonResident(flexResponse);
        if (multiCifNonResident) {
            return RegisterCaseTypeEnum.MULTI_CIF_NON_RESIDENT;
        }
        List<String> userFlexCifsMatchedByPhone =
                RegistrationUtil.provideUserFlexCifsMatchedByPhone(
                        checkUserRequisite.getPhone(),
                        flexResponse.getData());
        List<Customer> matchedIndividualCustomers =
                getIndividualCustomers(flexResponse, userFlexCifsMatchedByPhone);

        if (userFlexCifsMatchedByPhone.isEmpty()) {
            checkUserByPinAndPhone(checkUserRequisite);
            return RegisterCaseTypeEnum.IDENTIFIED_USER_CIF_PHONE_MISMATCH;
        } else if (matchedIndividualCustomers.isEmpty()) {
            return RegisterCaseTypeEnum.CORPORATE_CUSTOMER;

        }
        return RegisterCaseTypeEnum.REGISTER_PIN_SUCCESS;
    }

    private boolean checkMultiCifNonResident(FlexResponseDTO<List<DataDTO>> flexResponse) {
        //Business logic
        return !flexResponse.getData().isEmpty();
    }

    private List<Customer> getIndividualCustomers(FlexResponseDTO<List<DataDTO>> flexResponse,
                                                  List<String> userFlexCifsMatchedByPhone) {
        //Business logic
        return new ArrayList<>();
    }

    private void checkUserByPinAndBirthDate(CheckUserRequisiteDTO checkUserRequisite) {
        try {
            //Business logic
        } catch (BadRequestException badRequestException) {
            throw new BadRequestException(BadRequestErrorEnum.PIN_BIRTHDATE_MISMATCH);
        }
    }

    private void checkUserByPinAndPhone(CheckUserRequisiteDTO checkUserRequisite) {
        try {
            //Business logic
            if (StringUtils.isBlank(checkUserRequisite.getBirthdate())) {
                throw new BadRequestException(BadRequestErrorEnum.INVALID_CREDENTIALS);
            }
        } catch (BadRequestException badRequestException) {
            throw new BadRequestException(BadRequestErrorEnum.INVALID_CREDENTIALS);
        }
    }

    private User checkCifPhoneForUserUpdate(User userFromProvider, Consumer consumer,
                                            List<String> userFlexCifs, List<UserCif> userCifs) {

        if (Objects.isNull(userFlexCifs) || userFlexCifs.isEmpty()) {
            throw new BadRequestException(BadRequestErrorEnum.DUPLICATE_PHONE_NOT_ALLOWED);
        }

        int userCountWithSamePhoneAndCif = getUserCountWithSamePhoneAndCifSize(userFlexCifs, userCifs);

        if (userCountWithSamePhoneAndCif == ONE) {
            for (UserCif userCif : userCifs) {
                for (String flexCif : userFlexCifs) {
                    if (userCif.getCif().equals(flexCif)) {
                        return userService.updateUser(userCif.getUser(), userFromProvider, consumer);
                    }
                }
            }
        }

        throw new BadRequestException(BadRequestErrorEnum.DUPLICATE_PHONE_NOT_ALLOWED);
    }

    private int getUserCountWithSamePhoneAndCifSize(List<String> userFlexCifs, List<UserCif> userCifs) {
        List<String> userListWithSamePhoneAndCif = new ArrayList<>();
        for (UserCif userCif : userCifs) {
            for (String flexCif : userFlexCifs) {
                if (userCif.getCif().equals(flexCif)) {
                    userListWithSamePhoneAndCif.add(userCif.getUser().getId().toString());
                }
            }
        }
        return userListWithSamePhoneAndCif.size();
    }

}
