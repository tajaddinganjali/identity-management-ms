package com.management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechnicalException;
import com.management.identity.flex.customerreader.dto.ContactDetailsDTO;
import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.customerreader.dto.PersonalDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.service.IdentityService;
import com.management.identity.service.TokenService;
import com.management.limit.LimitService;
import com.management.model.Device;
import com.management.model.FixOtpDefinition;
import com.management.model.OtpDefinition;
import com.management.model.OtpLog;
import com.management.model.User;
import com.management.otp.dto.DeviceLimitDTO;
import com.management.otp.dto.OTPResponseDTO;
import com.management.otp.dto.SendRegisterOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpRequestDTO;
import com.management.otp.dto.VerifyOtpRequisiteDTO;
import com.management.otp.enums.OtpLimitTypeEnum;
import com.management.otp.repository.OtpLogRepository;
import com.management.otp.repository.OtpRepository;
import com.management.otp.service.CacheService;
import com.management.otp.service.DeviceService;
import com.management.otp.service.impl.OtpServiceImpl;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.register.dto.internal.HeaderInfoDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.RegisterCaseTypeEnum;
import com.management.template.dto.TemplateResponse;
import com.management.template.repository.TemplateRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OtpServiceUnitTest {

    @Mock
    private CacheService cacheService;
    @Mock
    private TokenService tokenService;
    @Mock
    private LimitService limitService;
    @Mock
    private OtpRepository otpRepository;
    @Mock
    private OtpLogRepository otpLogRepository;
    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private IdentityService identityService;
    @Mock
    private DeviceService deviceService;
    @InjectMocks
    private OtpServiceImpl otpService;

    @ParameterizedTest
    @EnumSource(value = RegisterCaseTypeEnum.class, names = {"REGISTER_PIN_SUCCESS"})
    void otpVerify(RegisterCaseTypeEnum registerCaseTypeEnum) {
        int deviceLimit = 500;
        ReflectionTestUtils.setField(otpService, "deviceLimit", deviceLimit);
        List<DeviceLimitDTO> deviceLimitDTO = List.of(DeviceLimitDTO.builder().build());
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setDeviceCode("code");
        RegisterCacheDTO registerCacheDTO = RegisterCacheDTO.builder()
                .registerCaseType(registerCaseTypeEnum)
                .pin("pin")
                .consumerType(ConsumerTypeEnum.MOBILE)
                .phone("99455555555")
                .device(deviceInfoDTO)
                .build();
        User user = User.builder().id(UUID.randomUUID()).build();
        VerifyOtpRequisiteDTO verifyOtpRequisiteDTO = VerifyOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder().consumer(ConsumerTypeEnum.MOBILE).token("Bearer ").build())
                .verifyOtpRequest(VerifyOtpRequestDTO.builder().otp("111111").build())
                .build();
        Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Mockito.when(cacheService.hasKey(anyString())).thenReturn(true);
        Mockito.when(cacheService.getValue(anyString())).thenReturn(registerCacheDTO);
        Mockito.when(cacheService.getSecret(anyString())).thenReturn("secret");
        Mockito.when(identityService.createDBUser(any())).thenReturn(user);
        Mockito.doNothing().when(limitService).checkLimit(any());
        Mockito.doNothing().when(limitService).doWrongAttempt(anyString(), any());
        Mockito.when(deviceService.getDevicesByUser(Mockito.any())).thenReturn(deviceLimitDTO);
        Mockito.when(deviceService.saveOrUpdateForRegistration(Mockito.any(), Mockito.any()))
                .thenReturn(Device.builder().id(UUID.randomUUID()).build());
        Assertions.assertDoesNotThrow(() -> otpService.otpVerify(verifyOtpRequisiteDTO));
    }

    @ParameterizedTest
    @EnumSource(value = RegisterCaseTypeEnum.class, names = {"IDENTIFIED_USER_NOT_FOUND_FLEX",
            "IDENTIFIED_USER_CIF_PHONE_MISMATCH", "CORPORATE_CUSTOMER", "MORE_THAN_ACCEPTED_CIF",
            "REGISTER_CARD_SUCCESS"})
    @MockitoSettings(strictness = Strictness.LENIENT)
    void otpVerify_badRequest(RegisterCaseTypeEnum registerCaseTypeEnum) {
        RegisterCacheDTO registerCacheDTO = RegisterCacheDTO.builder()
                .registerCaseType(registerCaseTypeEnum)
                .pin("pin")
                .phone("phone")
                .build();
        VerifyOtpRequisiteDTO verifyOtpRequisiteDTO = VerifyOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder().token("Bearer ").build())
                .verifyOtpRequest(VerifyOtpRequestDTO.builder().otp("111111").build())
                .build();
        ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();
        contactDetailsDTO.setMobile1("994556667788");
        PersonalDTO personalDTO = new PersonalDTO();
        personalDTO.setContactDetails(contactDetailsDTO);
        DataDTO dataDTO = new DataDTO();
        dataDTO.setPersonal(personalDTO);
        FlexResponseDTO<List<DataDTO>> flexResponseDTO = FlexResponseDTO.<List<DataDTO>>builder()
                .data(List.of(dataDTO))
                .build();
        Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Mockito.when(cacheService.hasKey(anyString())).thenReturn(true);
        Mockito.when(cacheService.getValue(anyString())).thenReturn(registerCacheDTO);
        Mockito.when(cacheService.getSecret(anyString())).thenReturn("secret");
        Mockito.when(identityService.getFlexCustomer(anyString())).thenReturn(flexResponseDTO);
        Mockito.doNothing().when(limitService).checkLimit(any());
        Mockito.doNothing().when(limitService).doWrongAttempt(anyString(), any());
        Assertions.assertThrows(BadRequestException.class, () -> otpService.otpVerify(verifyOtpRequisiteDTO));
    }

    @Test
    void otpVerify_fail() {
        VerifyOtpRequisiteDTO verifyOtpRequisiteDTO = VerifyOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder().token("Bearer ").build())
                .verifyOtpRequest(VerifyOtpRequestDTO.builder().otp("111111").build())
                .build();
        Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Assertions.assertThrows(GeneralTechnicalException.class, () -> otpService.otpVerify(verifyOtpRequisiteDTO));
    }

    @Test
    void checkOtpLimit() {
        Mockito.doNothing().when(otpRepository).checkSmsLimit(any(), anyString());
        Assertions.assertDoesNotThrow(() -> otpService.checkOtpLimit(OtpLimitTypeEnum.REGISTER_SMS_LIMIT, "phone"));
    }

    @Test
    void resendRegisterOtp_local() {
        String smsTitle = "ABB";
        ReflectionTestUtils.setField(otpService, "smsTitle", smsTitle);
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setDeviceCode("code");
        RegisterCacheDTO registerCacheDTO = RegisterCacheDTO.builder()
                .registerCaseType(RegisterCaseTypeEnum.REGISTER_PIN_SUCCESS)
                .pin("pin")
                .device(deviceInfoDTO)
                .phone("994556667788")
                .build();
        SendRegisterOtpRequisiteDTO sendRegisterOtpRequisiteDTO = SendRegisterOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder()
                        .token("Bearer ")
                        .consumer(ConsumerTypeEnum.MOBILE)
                        .build())
                .build();
        Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Mockito.when(cacheService.hasKey(anyString())).thenReturn(true);
        Mockito.when(cacheService.getValue(anyString())).thenReturn(registerCacheDTO);
        Mockito.doNothing().when(limitService).checkLimit(any());
        Mockito.doNothing().when(cacheService).delete(anyString());
        Mockito.when(tokenService.generateToken(Mockito.any())).thenReturn("Bearer  ");
        Mockito.when(cacheService.findFixOtpDefinition(anyString()))
                .thenReturn(Optional.of(FixOtpDefinition.builder().otp("111111").build()));
        Assertions.assertDoesNotThrow(() -> otpService.resendRegisterOtp(sendRegisterOtpRequisiteDTO));
    }

    @Test
    void resendRegisterOtp_prod() {
        String smsTitle = "ABB";
        ReflectionTestUtils.setField(otpService, "smsTitle", smsTitle);
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setDeviceCode("code");
        RegisterCacheDTO registerCacheDTO = RegisterCacheDTO.builder()
                .registerCaseType(RegisterCaseTypeEnum.REGISTER_PIN_SUCCESS)
                .pin("pin")
                .phone("994556667788")
                .device(deviceInfoDTO)
                .build();
        SendRegisterOtpRequisiteDTO sendRegisterOtpRequisiteDTO = SendRegisterOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder()
                        .token("Bearer ")
                        .consumer(ConsumerTypeEnum.MOBILE)
                        .build())
                .build();
        Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Mockito.when(cacheService.hasKey(anyString())).thenReturn(true);
        Mockito.when(cacheService.getValue(anyString())).thenReturn(registerCacheDTO);
        Mockito.doNothing().when(limitService).checkLimit(any());
        Mockito.doNothing().when(cacheService).delete(anyString());
        Mockito.when(tokenService.generateToken(Mockito.any())).thenReturn("Bearer  ");
        Mockito.when(cacheService.findFixOtpDefinition(anyString()))
                .thenReturn(Optional.of(FixOtpDefinition.builder().otp("111111").build()));
        Mockito.when(cacheService.createTOTP(anyString())).thenReturn("111111");
        Mockito.when(cacheService.findOtpDefinition(anyString()))
                .thenReturn(OtpDefinition.builder().templateId(UUID.randomUUID()).build());
        Mockito.when(templateRepository.getRenderedData(any()))
                .thenReturn(TemplateResponse.builder().content("content").build());
        Mockito.when(otpRepository.sendSMS(any()))
                .thenReturn(OTPResponseDTO.builder().messageID("id").result("result").otp("111111").build());
        Mockito.when(otpLogRepository.save(any())).thenReturn(OtpLog.builder().build());
        Assertions.assertDoesNotThrow(() -> otpService.resendRegisterOtp(sendRegisterOtpRequisiteDTO));
    }

    @Test
    void resendRegisterOtp_fail() {
        String smsTitle = "ABB";
        ReflectionTestUtils.setField(otpService, "smsTitle", smsTitle);
        SendRegisterOtpRequisiteDTO sendRegisterOtpRequisiteDTO = SendRegisterOtpRequisiteDTO.builder()
                .headerInfo(HeaderInfoDTO.builder()
                        .token("Bearer ")
                        .consumer(ConsumerTypeEnum.MOBILE)
                        .build())
                .build();
        //       Mockito.doNothing().when(tokenService).verifyVerificationToken(Mockito.anyString());
        Mockito.when(cacheService.hasKey(anyString())).thenReturn(false);
        Assertions.assertThrows(GeneralTechnicalException.class,
                () -> otpService.resendRegisterOtp(sendRegisterOtpRequisiteDTO));
    }

}
