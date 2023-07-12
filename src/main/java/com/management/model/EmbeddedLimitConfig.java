package com.management.model;

import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.model.converter.LimitServiceTypeAttrConverter;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class EmbeddedLimitConfig {

    private Integer attemptCount;

    private Integer lockTime;

    @Convert(converter = LimitServiceTypeAttrConverter.class)
    private LimitServiceTypeEnum service;

    @Builder
    public EmbeddedLimitConfig(Integer attemptCount, Integer lockTime, LimitServiceTypeEnum service) {
        this.attemptCount = attemptCount;
        this.lockTime = lockTime;
        this.service = service;
    }

}
