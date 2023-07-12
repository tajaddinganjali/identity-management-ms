package com.management.service;

import com.management.exception.BadRequestException;
import com.management.identity.dto.CheckUserRequisiteDTO;
import com.management.identity.flex.customerreader.dto.BaseDTO;
import com.management.identity.flex.customerreader.dto.ContactDetailsDTO;
import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.customerreader.dto.IdentityDTO;
import com.management.identity.flex.customerreader.dto.PersonalDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.flex.service.FlexService;
import com.management.identity.service.ConsumerService;
import com.management.identity.service.UserCifService;
import com.management.identity.service.UserRegistrationService;
import com.management.identity.service.UserService;
import com.management.identity.service.impl.IdentityServiceImpl;
import com.management.limit.LimitService;
import com.management.model.Channel;
import com.management.model.Consumer;
import com.management.model.User;
import com.management.model.UserCif;
import com.management.model.UserRegistration;
import com.management.model.enums.UserStatus;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.service.ReferralService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdentityServiceUnitTest {


    @Mock
    private FlexService flexService;
    @Mock
    private LimitService limitService;
    @Mock
    private ConsumerService consumerService;
    @Mock
    private UserService userService;
    @Mock
    private UserCifService userCifService;
    @Mock
    private UserRegistrationService userRegistrationService;
    @InjectMocks
    private IdentityServiceImpl identityService;
    @Mock
    private ReferralService referralService;

    @Test
    void identifyUser() {
        DataDTO dataDTO = new DataDTO();
        dataDTO.setPersonal(getPersonal());
        dataDTO.setBase(getBase());
        List<DataDTO> dataDTOList = new ArrayList<>();
        dataDTOList.add(dataDTO);
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>(dataDTOList, "status");
        final CheckUserRequisiteDTO checkUserRequisiteDTO = CheckUserRequisiteDTO.builder()
                .pin("AAA1111")
                .phone("994555555555")
                .build();
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertDoesNotThrow(() -> identityService.identifyUser(checkUserRequisiteDTO));
    }

    @Test
    void identifyUser_notFoundInFlex() {
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        final CheckUserRequisiteDTO checkUserRequisiteDTO = CheckUserRequisiteDTO.builder()
                .pin("AAA1111")
                .phone("994555555555")
                .build();
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertThrows(BadRequestException.class, () -> identityService.identifyUser(checkUserRequisiteDTO));
    }

    @Test
    void identifyUser_withBirthDate() {
        DataDTO dataDTO = new DataDTO();
        dataDTO.setBase(getBase());
        dataDTO.setPersonal(getPersonal());
        List<DataDTO> dataDTOList = new ArrayList<>();
        dataDTOList.add(dataDTO);
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>(dataDTOList, "status");
        final CheckUserRequisiteDTO checkUserRequisiteDTO = CheckUserRequisiteDTO.builder()
                .pin("AAA1111")
                .phone("994555555555")
                .birthdate("10/10/2000")
                .build();
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertDoesNotThrow(() -> identityService.identifyUser(checkUserRequisiteDTO));
    }

    @Test
    void identifyUser_notFoundInFlexWithBirthDate() {
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        final CheckUserRequisiteDTO checkUserRequisiteDTO = CheckUserRequisiteDTO.builder()
                .pin("AAA1111")
                .phone("994555555555")
                .birthdate("10/10/2000")
                .build();
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertDoesNotThrow(() -> identityService.identifyUser(checkUserRequisiteDTO));
    }

    @Test
    void getFlexCustomer() {
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertDoesNotThrow(() -> identityService.getFlexCustomer("pin"));
    }


    @Test
    void createDBUser() {
        boolean checkPhoneEnabled = true;
        ReflectionTestUtils.setField(identityService, "checkPhoneEnabled", checkPhoneEnabled);
        DataDTO dataDTO = new DataDTO();
        dataDTO.setBase(getBase());
        dataDTO.setPersonal(getPersonal());
        List<DataDTO> dataDTOList = new ArrayList<>();
        dataDTOList.add(dataDTO);
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        flexResponse.setData(dataDTOList);
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Mockito.when(consumerService.findById(Mockito.anyString())).thenReturn(Consumer.builder().build());
        Mockito.when(userService.findByPin(Mockito.anyString()))
                .thenReturn(Optional.of(User.builder().status(UserStatus.REGISTERING).build()));
        Mockito.when(userCifService.getCifsByPhone(Mockito.anyString()))
                .thenReturn(List.of(UserCif.builder()
                        .user(User.builder().id(UUID.randomUUID()).build())
                        .cif("1400782")
                        .build()));
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(User.builder().id(UUID.randomUUID()).cif("1400782").build());
        Mockito.when(userService.save(Mockito.any())).thenReturn(User.builder().id(UUID.randomUUID()).build());
        Mockito.when(userRegistrationService.save(Mockito.any())).thenReturn(UserRegistration.builder().build());
        Assertions.assertDoesNotThrow(() -> identityService.createDBUser(
                RegisterCacheDTO.builder()
                        .pin("pin").phone("994507935313").consumerType(ConsumerTypeEnum.MOBILE).build()));
    }

    @Test
    void createDBUser_pinPhoneMismatch() {
        DataDTO dataDTO = new DataDTO();
        dataDTO.setBase(getBase());
        dataDTO.setPersonal(getPersonal());
        List<DataDTO> dataDTOList = new ArrayList<>();
        dataDTOList.add(dataDTO);
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        flexResponse.setData(dataDTOList);
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Assertions.assertThrows(BadRequestException.class, () -> identityService.createDBUser(
                RegisterCacheDTO.builder()
                        .pin("pin").phone("994507935314").consumerType(ConsumerTypeEnum.MOBILE).build()));
    }

    @Test
    void createDBUser_duplicatePhone() {
        boolean checkPhoneEnabled = true;
        ReflectionTestUtils.setField(identityService, "checkPhoneEnabled", checkPhoneEnabled);
        DataDTO dataDTO = new DataDTO();
        dataDTO.setBase(getBase());
        dataDTO.setPersonal(getPersonal());
        List<DataDTO> dataDTOList = new ArrayList<>();
        dataDTOList.add(dataDTO);
        FlexResponseDTO<List<DataDTO>> flexResponse = new FlexResponseDTO<>();
        flexResponse.setData(dataDTOList);
        Mockito.when(flexService.getCustomer(Mockito.anyString())).thenReturn(flexResponse);
        Mockito.when(consumerService.findById(Mockito.anyString())).thenReturn(Consumer.builder().channel(Channel.MOBILE).build());
        Mockito.when(userService.findByPin(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(userCifService.getCifsByPhone(Mockito.anyString()))
                .thenReturn(List.of(UserCif.builder()
                        .user(User.builder().id(UUID.randomUUID()).build())
                        .cif("1400782").build()));
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(User.builder().id(UUID.randomUUID()).cif("1400782").build());
        Mockito.when(userService.save(Mockito.any())).thenReturn(User.builder().id(UUID.randomUUID()).build());
        Mockito.when(userRegistrationService.save(Mockito.any())).thenReturn(UserRegistration.builder().build());
        Assertions.assertDoesNotThrow(() -> identityService.createDBUser(
                RegisterCacheDTO.builder()
                        .pin("pin").phone("994507935313").consumerType(ConsumerTypeEnum.MOBILE).build()));
    }

    private BaseDTO getBase() {
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setCustomerType("I");
        baseDTO.setClassification("STANDART");
        baseDTO.setCustomerNumber("1400782");
        baseDTO.setName("RAISA");
        return baseDTO;
    }

    private PersonalDTO getPersonal() {
        PersonalDTO personalDTO = new PersonalDTO();
        personalDTO.setDateOfBirth("1981-08-29");
        personalDTO.setFirstName("RAISA");
        personalDTO.setLastName("RAIS");
        personalDTO.setMiddleName("MAMMADOVA");
        personalDTO.setContactDetails(getContacts());
        personalDTO.setId(getIdentity());
        return personalDTO;
    }

    private ContactDetailsDTO getContacts() {
        ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
        contactDetailsDTO.setMobile1("994507935313");
        return contactDetailsDTO;
    }

    private IdentityDTO getIdentity() {
        IdentityDTO identityDTO = new IdentityDTO();
        identityDTO.setIdNumber("AZE");
        identityDTO.setIssuingDate("2000-01-01");
        identityDTO.setIssuedByAuthority("Auth");
        identityDTO.setMaturityDate("2000-01-01");
        identityDTO.setPinCode("1234PJ6");
        identityDTO.setPersonalIdType("ID");
        return identityDTO;
    }

}
