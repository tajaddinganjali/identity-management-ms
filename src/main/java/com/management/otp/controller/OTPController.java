package com.management.otp.controller;

import com.management.otp.dto.SendOtpResponseDTO;
import com.management.otp.dto.SendRegisterOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpRequestDTO;
import com.management.otp.dto.VerifyOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpResponseDTO;
import com.management.otp.service.OtpService;
import com.management.register.dto.ApiResponseDTO;
import com.management.register.dto.internal.HeaderInfoDTO;
import com.management.register.enums.ConsumerTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
@Api(value = "Service for One Time Password (OTP) actions.", tags = {"OTP"})
@RequiredArgsConstructor
public class OTPController {

    private final OtpService otpService;

    @PostMapping("/verify")
    @ApiOperation(value = "Verification end-point for the OTP passwords.",
            response = VerifyOtpResponseDTO.class,
            httpMethod = "POST")
    public ApiResponseDTO<VerifyOtpResponseDTO> otpverify(
            @RequestHeader(value = "accept-language", defaultValue = "az") String lang,
            @RequestHeader(value = "X-Consumer", required = false, defaultValue = "IBA_MOBILE")
                    ConsumerTypeEnum consumerType,
            @RequestHeader(value = "X-App-Version", required = false) String appVersion,
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid VerifyOtpRequestDTO verifyOtpRequest) {

        return otpService.otpVerify(VerifyOtpRequisiteDTO.builder()
                .verifyOtpRequest(verifyOtpRequest)
                .headerInfo(HeaderInfoDTO.builder()
                        .lang(lang)
                        .consumer(consumerType)
                        .appVersion(appVersion)
                        .token(authorization)
                        .build())
                .build());
    }

    @PostMapping("/resend-register")
    @ApiOperation(value = "send end-point for the OTP passwords.",
            response = SendOtpResponseDTO.class,
            httpMethod = "POST")
    public ApiResponseDTO<SendOtpResponseDTO> resendRegisterOtp(
            @RequestHeader(value = "accept-language", defaultValue = "az") String lang,
            @RequestHeader(value = "X-Consumer", required = false, defaultValue = "IBA_MOBILE")
                    ConsumerTypeEnum consumerType,
            @RequestHeader(value = "X-App-Version", required = false) String appVersion,
            @RequestHeader("Authorization") String authorization) {

        return ApiResponseDTO.<SendOtpResponseDTO>builder()
                .data(otpService.resendRegisterOtp(SendRegisterOtpRequisiteDTO
                        .builder()
                        .headerInfo(HeaderInfoDTO.builder()
                                .appVersion(appVersion)
                                .consumer(consumerType)
                                .lang(lang)
                                .token(authorization)
                                .build())
                        .build()))
                .build();
    }

}
