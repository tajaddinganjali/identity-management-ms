package com.management.otp.service.impl;

import com.management.exception.BadRequestErrorEnum;
import com.management.exception.BadRequestException;
import com.management.exception.GeneralTechErrorEnum;
import com.management.exception.GeneralTechnicalException;
import com.management.identity.dto.VerificationTokenRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.IdentityService;
import com.management.identity.service.TokenService;
import com.management.limit.LimitService;
import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.model.Device;
import com.management.model.FixOtpDefinition;
import com.management.model.OtpDefinition;
import com.management.model.OtpLog;
import com.management.model.User;
import com.management.otp.dto.DeviceLimitDTO;
import com.management.otp.dto.OTPRequestDTO;
import com.management.otp.dto.OTPResponseDTO;
import com.management.otp.dto.SendOtpRequisiteDTO;
import com.management.otp.dto.SendOtpResponseDTO;
import com.management.otp.dto.SendRegisterOtpRequisiteDTO;
import com.management.otp.dto.SmsDTO;
import com.management.otp.dto.VerifyOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpResponseDTO;
import com.management.otp.enums.OtpLimitTypeEnum;
import com.management.otp.repository.OtpLogRepository;
import com.management.otp.repository.OtpRepository;
import com.management.otp.service.CacheService;
import com.management.otp.service.DeviceService;
import com.management.otp.service.OtpService;
import com.management.register.dto.ApiResponseDTO;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.KafkaEventType;
import com.management.template.dto.TemplateRequest;
import com.management.template.dto.TemplateResponse;
import com.management.template.repository.TemplateRepository;
import com.management.util.PhoneUtil;
import com.management.util.constant.OtpDefinitionEnum;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private static final String DEVICE_LIMIT = "device_limit";
    private final CacheService cacheService;
    private final TokenService tokenService;
    private final LimitService limitService;
    private final OtpRepository otpRepository;
    private final OtpLogRepository otpLogRepository;
    private final TemplateRepository templateRepository;
    private final IdentityService identityService;
    private final DeviceService deviceService;
    @Value("${sms.title}")
    public String smsTitle;
    @Value("${iam-svc.verification-token.cache-time-in-min}")
    private Integer expireTimeMinutes;
    @Value("${device.limit}")
    private int deviceLimit;

    public OtpServiceImpl(CacheService cacheService, TokenService tokenService, LimitService limitService,
                          OtpRepository otpRepository, OtpLogRepository otpLogRepository,
                          TemplateRepository templateRepository,
                          IdentityService identityService, DeviceService deviceService) {
        this.cacheService = cacheService;
        this.tokenService = tokenService;
        this.limitService = limitService;
        this.otpRepository = otpRepository;
        this.otpLogRepository = otpLogRepository;
        this.templateRepository = templateRepository;
        this.identityService = identityService;
        this.deviceService = deviceService;
    }

    @Override
    public ApiResponseDTO<VerifyOtpResponseDTO> otpVerify(VerifyOtpRequisiteDTO verifyOtpRequisite) {
        String verificationToken = verifyOtpRequisite.getHeaderInfo().getToken()
                .replace("Bearer ", "");
        tokenService.verifyVerificationToken(verificationToken);
        if (cacheService.hasKey(verificationToken)) {
            RegisterCacheDTO registerCache = (RegisterCacheDTO) cacheService.getValue(verificationToken);
            if (Objects.nonNull(registerCache) && Objects.nonNull(registerCache.getRegisterCaseType())) {
                limitService.checkLimit(LimitServiceTypeEnum
                        .getEnumMap(List.of(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP),
                                registerCache.getPin()));
                validateOtp(registerCache, verifyOtpRequisite.getVerifyOtpRequest().getOtp());
                return handleRegisterCaseType(verifyOtpRequisite, registerCache);
            } else {
                throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "unknown_token");
        }
    }

    private void validateOtp(RegisterCacheDTO registerCache, String requestOtp) {
        boolean isValid = isValidOtp(registerCache.getPin().concat(registerCache.getPhone()), requestOtp);
        if (!isValid) {
            limitService.doWrongAttempt(registerCache.getPin(), LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP);
        }
    }

    private String getDeviceId(VerifyOtpRequisiteDTO verifyOtpRequisite, User user, DeviceInfoDTO deviceInfo) {
        Device device;
        String deviceId = null;
        if (verifyOtpRequisite.getHeaderInfo().getConsumer() == ConsumerTypeEnum.MOBILE) {
            checkDeviceLimit(user);

            validateDeviceRequest(deviceInfo);

            device = deviceService.saveOrUpdateForRegistration(deviceInfo, user);

            deviceId = device.getId().toString();
        }
        return deviceId;
    }

    @Override
    public SendOtpResponseDTO resendRegisterOtp(SendRegisterOtpRequisiteDTO sendOtpRequisite) {
        String previousToken = sendOtpRequisite.getHeaderInfo().getToken().replace("Bearer ", "");
        tokenService.verifyVerificationToken(previousToken);
        String newToken;
        if (cacheService.hasKey(previousToken)) {
            RegisterCacheDTO registerCache = (RegisterCacheDTO) cacheService.getValue(previousToken);
            if (Objects.nonNull(registerCache)) {
                limitService.checkLimit(LimitServiceTypeEnum.getEnumMap(
                        List.of(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP), registerCache.getPin()));
                newToken = tokenService.createVerificationToken(VerificationTokenRequisiteDTO
                        .builder().consumer(sendOtpRequisite.getHeaderInfo().getConsumer())
                        .phone(registerCache.getPhone())
                        .deviceId(registerCache.getDevice().getDeviceCode())
                        .profileType(ProfileType.CUSTOMER).build());
                cacheService.delete(previousToken);
                OTPResponseDTO otpResponse = sendOtp(SendOtpRequisiteDTO.builder()
                        .otpDefinitionId(
                                OtpDefinitionEnum.getOtpDefinitionId(sendOtpRequisite.getHeaderInfo().getConsumer()))
                        .pin(registerCache.getPin()).phone(registerCache.getPhone())
                        .otpLimitTypeEnum(OtpLimitTypeEnum.REGISTER_SMS_LIMIT).build());
                cacheService.setValue(newToken, registerCache, expireTimeMinutes);
                return SendOtpResponseDTO
                        .builder().verificationToken(newToken).isLastSms(otpResponse.isLastSms()).build();
            } else {
                throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR);
            }
        } else {
            throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR, "unknown_token");
        }
    }

    /**
     * we use findFixOtpDefinition
     * in test environment to enable testing
     * in prod environment to enable testing for stores
     */

    @Override
    public OTPResponseDTO sendOtp(SendOtpRequisiteDTO sendOtpRequisite) {
        String phoneWithoutCountryCode = PhoneUtil.removePhoneNumberCountryCode(sendOtpRequisite.getPhone());
        Optional<FixOtpDefinition> fixOtpDefinition =
                cacheService.findFixOtpDefinition(sendOtpRequisite.getPin());
        if (fixOtpDefinition.isPresent()) {
            OTPResponseDTO otpResponse = OTPResponseDTO.builder()
                    .messageID("")
                    .result("")
                    .otp(fixOtpDefinition.get().getOtp())
                    .build();
            checkLastSms(sendOtpRequisite, otpResponse);
            return otpResponse;
        } else {
            String otp = cacheService.createTOTP(sendOtpRequisite.getPin().concat(sendOtpRequisite.getPhone()));
            String messageText = getMessageText(sendOtpRequisite.getData(), otp, sendOtpRequisite.getOtpDefinitionId());
            String formattedPhone = String.format("994%s", phoneWithoutCountryCode.substring(1));
            SmsDTO sms = SmsDTO.builder()
                    .title(smsTitle)
                    .text(messageText)
                    .build();
            OTPRequestDTO otpRequestDTO = OTPRequestDTO.builder()
                    .phoneNumber(formattedPhone)
                    .sms(sms)
                    .otpLimitType(sendOtpRequisite.getOtpLimitTypeEnum())
                    .build();
            OTPResponseDTO otpResponse = otpRepository.sendSMS(otpRequestDTO);
            checkLastSms(sendOtpRequisite, otpResponse);
            log.info("otp send {}. taskID: {}", otpResponse.getResult(), otpResponse.getMessageID());
            String maskOtp =
                    PhoneUtil.maskNumber(otp, 2, otp.length() - 2, '*');
            String maskMessageText = messageText.replace(otp, maskOtp);
            OtpLog otpLog = OtpLog.builder()
                    .pin(sendOtpRequisite.getPin())
                    .otpDefinitionId(UUID.fromString(sendOtpRequisite.getOtpDefinitionId())).smsText(maskMessageText)
                    .phone(formattedPhone).refId(otpResponse.getMessageID()).build();
            log.info("logging send otp");
            otpLogRepository.save(otpLog);
            otpResponse.setOtp(otp);
            return otpResponse;
        }
    }

    private void checkLastSms(SendOtpRequisiteDTO sendOtpRequisite, OTPResponseDTO otpResponse) {
        try {
            limitService.doSuccessAttempt(
                    LimitServiceTypeEnum.getEnumMap(List.of(LimitServiceTypeEnum.REGISTER_AUTHORIZATION_WITH_SMS),
                            sendOtpRequisite.getPhone()));
        } catch (BadRequestException exception) {
            if (exception.getCode().equalsIgnoreCase(BadRequestErrorEnum.LAST_SMS_FOR_TEMPORARY_BLOCK.getCode())) {
                otpResponse.setLastSms(true);
            } else {
                throw exception;
            }
        }
    }

    @Override
    public void checkOtpLimit(OtpLimitTypeEnum otpLimitTypeEnum, String phone) {
        otpRepository.checkSmsLimit(otpLimitTypeEnum, phone);
    }

    private ApiResponseDTO<VerifyOtpResponseDTO> handleRegisterCaseType(VerifyOtpRequisiteDTO verifyOtpRequisite,
                                                                        RegisterCacheDTO registerCache) {
        switch (registerCache.getRegisterCaseType()) {
            case REGISTER_PIN_SUCCESS:
                return handleRegisterPinSuccess(verifyOtpRequisite, registerCache);
            case REGISTER_CARD_SUCCESS:
                throw new BadRequestException(BadRequestErrorEnum.BAD_REQUEST);
            case IDENTIFIED_USER_NOT_FOUND_FLEX:
                throw new BadRequestException(BadRequestErrorEnum.IDENTIFIED_USER_NOT_FOUND_FLEX);
            case IDENTIFIED_USER_CIF_PHONE_MISMATCH:
                List<String> maskedHhoneNumbers =
                        PhoneUtil.getMaskedPhoneNumbers(identityService
                                .getFlexCustomer(registerCache.getPin()).getData());
                String phonesWithContryCode =
                        maskedHhoneNumbers.stream().map(PhoneUtil::addPhoneNumberCountryCodeIntegration)
                                .collect(Collectors.joining(","));
                throw new BadRequestException(BadRequestErrorEnum.IDENTIFIED_USER_CIF_PHONE_MISMATCH,
                        "Pin phone mismatch", phonesWithContryCode);
            case CORPORATE_CUSTOMER:
                throw new BadRequestException(BadRequestErrorEnum.CORPORATE_CUSTOMER);
            case MORE_THAN_ACCEPTED_CIF:
                throw new BadRequestException(BadRequestErrorEnum.MORE_THAN_ACCEPTED_CIF);
            case MULTI_CIF_NON_RESIDENT:
                throw new BadRequestException(BadRequestErrorEnum.MULTI_CIF_NON_RESIDENT);
            default:
                throw new GeneralTechnicalException(GeneralTechErrorEnum.GENERAL_SERVER_ERROR,
                        "unknown_register_case");
        }
    }

    private ApiResponseDTO<VerifyOtpResponseDTO> handleRegisterPinSuccess(
            VerifyOtpRequisiteDTO verifyOtpRequisite, RegisterCacheDTO registerCache) {
        limitService.doSuccessAttempt(LimitServiceTypeEnum.getEnumMap(
                Arrays.asList(LimitServiceTypeEnum.REGISTER_WITH_PIN_OTP, LimitServiceTypeEnum.LOGIN_CHECK_PASSCODE),
                registerCache.getPin()));
        User user = identityService.createDBUser(registerCache);
        String deviceId = getDeviceId(verifyOtpRequisite, user, registerCache.getDevice());
        String registrationToken = tokenService.createRegistrationToken(user.getId().toString(),
                verifyOtpRequisite.getHeaderInfo().getConsumer(), ProfileType.CUSTOMER, deviceId);
        cacheService.saveRegistrationTypeToCache(user.getId().toString(), deviceId,
                registerCache.getConsumerType().getConsumerId(), KafkaEventType.REGISTERED_BY_PIN);
        return ApiResponseDTO.<VerifyOtpResponseDTO>builder()
                .data(VerifyOtpResponseDTO.builder()
                        .registrationToken(registrationToken)
                        .userId(user.getId().toString())
                        .isValid(true)
                        .build())
                .build();
    }

    private String getMessageText(Map<String, Object> data, String otp, String definitionId) {
        if (Objects.isNull(data)) {
            data = new HashMap<>();
        }
        data.put("otp", otp);
        log.info("getting otp definition from db");
        OtpDefinition otpDefinition =
                cacheService.findOtpDefinition(definitionId);
        UUID templateId = otpDefinition.getTemplateId();
        TemplateRequest templateRequest = TemplateRequest.builder()
                .templateId(templateId.toString())
                .data(data).build();
        log.info("rendering data on render service");
        TemplateResponse templateResponse = templateRepository.getRenderedData(templateRequest);
        return templateResponse.getContent();
    }

    @Override
    public boolean isValidOtp(String key, String otp) {
        String secret = cacheService.getSecret(key);
        return cacheService.validateTOTP(otp, secret);
    }

    private void validateDeviceRequest(DeviceInfoDTO deviceInfo) {
        if (Objects.isNull(deviceInfo)
                || Objects.isNull(deviceInfo.getDeviceCode())) {
            throw new BadRequestException(BadRequestErrorEnum.DEVICE_CANNOT_BE_NULL);
        }
    }

    private void checkDeviceLimit(User user) {
        List<DeviceLimitDTO> userDeviceList = deviceService.getDevicesByUser(user);

        if (userDeviceList.size() >= deviceLimit) {
            Map<String, Object> deviceMap = new HashMap<>();

            deviceMap.put(DEVICE_LIMIT, deviceLimit);
            deviceMap.put("devices", userDeviceList);
            throw new BadRequestException(BadRequestErrorEnum.DEVICE_NOT_FOUND, null, deviceMap, deviceLimit);

        }
    }

}
