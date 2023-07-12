package com.management.register.dto.internal;

import com.management.register.enums.ConsumerTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HeaderInfoDTO {

    private ConsumerTypeEnum consumer;
    private String appVersion;
    private String lang;
    private String token;

}
