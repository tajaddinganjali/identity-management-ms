package com.management.controller;

import com.management.otp.controller.OTPController;
import com.management.otp.dto.SendOtpResponseDTO;
import com.management.otp.dto.VerifyOtpRequestDTO;
import com.management.otp.dto.VerifyOtpResponseDTO;
import com.management.otp.service.OtpService;
import com.management.register.dto.ApiResponseDTO;
import com.management.register.enums.ConsumerTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OTPControllerUnitTest {

    @Mock
    private OtpService otpService;
    @InjectMocks
    private OTPController otpController;

    @Test
    void otpverify() {
        ApiResponseDTO<VerifyOtpResponseDTO> verifyOtpResponseDTO = new ApiResponseDTO<>();
        verifyOtpResponseDTO.setData(VerifyOtpResponseDTO.builder().build());
        VerifyOtpRequestDTO verifyOtpRequestDTO = VerifyOtpRequestDTO.builder()
                .otp("otp")
                .build();
        Mockito.when(otpService.otpVerify(Mockito.any())).thenReturn(verifyOtpResponseDTO);
        Assertions.assertDoesNotThrow(() -> otpController.otpverify("lang",
                ConsumerTypeEnum.MOBILE, "version", "authorization", verifyOtpRequestDTO));
    }

    @Test
    void resendRegisterOtp() {
        Mockito.when(otpService.resendRegisterOtp(Mockito.any())).thenReturn(SendOtpResponseDTO.builder().build());
        Assertions.assertDoesNotThrow(() -> otpController.resendRegisterOtp("lang",
                ConsumerTypeEnum.MOBILE, "version", "authorization"));
    }

}
