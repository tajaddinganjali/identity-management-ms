package com.management.model.converter;

import com.management.limit.enums.LimitServiceTypeEnum;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LimitServiceTypeAttrConverter implements AttributeConverter<LimitServiceTypeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(LimitServiceTypeEnum attribute) {
        return attribute.getDbCode();
    }

    @Override
    public LimitServiceTypeEnum convertToEntityAttribute(Integer dbData) {
        return LimitServiceTypeEnum.findByDbCode(dbData);
    }

}
