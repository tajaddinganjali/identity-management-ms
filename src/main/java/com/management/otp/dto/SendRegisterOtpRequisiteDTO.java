package com.management.otp.dto;

import com.management.register.dto.internal.HeaderInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SendRegisterOtpRequisiteDTO {

    private SendOtpRequestDTO sendOtpRequest;
    private HeaderInfoDTO headerInfo;

}
