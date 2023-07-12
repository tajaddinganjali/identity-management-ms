package com.management.otp.service;

import com.management.otp.dto.OTPResponseDTO;
import com.management.otp.dto.SendOtpRequisiteDTO;
import com.management.otp.dto.SendOtpResponseDTO;
import com.management.otp.dto.SendRegisterOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpRequisiteDTO;
import com.management.otp.dto.VerifyOtpResponseDTO;
import com.management.otp.enums.OtpLimitTypeEnum;
import com.management.register.dto.ApiResponseDTO;

public interface OtpService {

    ApiResponseDTO<VerifyOtpResponseDTO> otpVerify(VerifyOtpRequisiteDTO verifyOtpRequisite);

    SendOtpResponseDTO resendRegisterOtp(SendRegisterOtpRequisiteDTO sendOtpRequisite);

    OTPResponseDTO sendOtp(SendOtpRequisiteDTO sendOtpRequisite);

    void checkOtpLimit(OtpLimitTypeEnum otpLimitTypeEnum, String phone);

    boolean isValidOtp(String key, String otp);

}
