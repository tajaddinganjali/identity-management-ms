package com.management.otp.dto;

import com.management.register.dto.internal.HeaderInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpRequisiteDTO {

    private VerifyOtpRequestDTO verifyOtpRequest;
    private HeaderInfoDTO headerInfo;

}
