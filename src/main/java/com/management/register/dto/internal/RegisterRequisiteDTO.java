package com.management.register.dto.internal;

import com.management.register.dto.RegisterPinRequestDTO;
import com.management.register.enums.RegisterCaseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequisiteDTO {

    private RegisterPinRequestDTO registerPinRequest;
    private HeaderInfoDTO headerInfo;
    private RegisterCaseTypeEnum registerCaseType;

}
