package com.management.register.dto;

import com.management.register.dto.internal.DeviceInfoDTO;
import com.management.register.enums.ConsumerTypeEnum;
import com.management.register.enums.RegisterCaseTypeEnum;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterCacheDTO implements Serializable {

    private static final long serialVersionUID = -8038471062922789289L;
    private String phone;
    private String pin;
    private RegisterCaseTypeEnum registerCaseType;
    private ConsumerTypeEnum consumerType;
    private DeviceInfoDTO device;
    private String userId;

}
