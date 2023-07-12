package com.management.register.strategy.impl;

import com.management.identity.dto.VerificationTokenRequisiteDTO;
import com.management.identity.enums.ProfileType;
import com.management.identity.service.TokenService;
import com.management.otp.dto.OTPResponseDTO;
import com.management.otp.dto.SendOtpRequisiteDTO;
import com.management.otp.enums.OtpLimitTypeEnum;
import com.management.otp.service.OtpService;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import com.management.register.strategy.RegistrationStrategy;
import com.management.util.constant.OtpDefinitionEnum;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service("mobile")
public class MobileRegistrationStrategyImpl implements RegistrationStrategy {

    private final TokenService tokenService;

    private final OtpService otpService;

    private final ValueOperations<String, Object> registerCacheTemplate;

    @Value("${iam-svc.verification-token.cache-time-in-min}")
    private Integer expireTimeMinutes;

    public MobileRegistrationStrategyImpl(TokenService tokenService,
                                          OtpService otpService, @Qualifier("commonRedisTemplate")
                                                  RedisTemplate<String, Object> redisTemplate) {
        this.tokenService = tokenService;
        this.otpService = otpService;
        this.registerCacheTemplate = redisTemplate.opsForValue();
    }


    @Override
    public RegisterPinResponseDTO registerWithPin(RegisterRequisiteDTO registerRequisite) {
        String verificationToken = tokenService.createVerificationToken(VerificationTokenRequisiteDTO
                .builder().consumer(registerRequisite.getHeaderInfo().getConsumer())
                .deviceId(registerRequisite.getRegisterPinRequest().getDevice().getDeviceCode())
                .phone(registerRequisite.getRegisterPinRequest().getPhone())
                .profileType(ProfileType.CUSTOMER).build());
        OTPResponseDTO otpResponse = otpService.sendOtp(SendOtpRequisiteDTO.builder()
                .otpDefinitionId(OtpDefinitionEnum.getOtpDefinitionId(registerRequisite.getHeaderInfo().getConsumer()))
                .phone(registerRequisite.getRegisterPinRequest().getPhone())
                .pin(registerRequisite.getRegisterPinRequest().getPin())
                .otpLimitTypeEnum(OtpLimitTypeEnum.REGISTER_SMS_LIMIT)
                .build());
        registerCacheTemplate.set(verificationToken,
                RegisterCacheDTO.builder()
                        .registerCaseType(registerRequisite.getRegisterCaseType())
                        .phone(registerRequisite.getRegisterPinRequest().getPhone())
                        .pin(registerRequisite.getRegisterPinRequest().getPin())
                        .device(registerRequisite.getRegisterPinRequest().getDevice())
                        .consumerType(registerRequisite.getHeaderInfo().getConsumer()),
                Duration.ofMinutes(expireTimeMinutes));
        return RegisterPinResponseDTO.builder()
                .verificationToken(verificationToken)
                .isLastSms(otpResponse.isLastSms())
                .build();
    }

}
