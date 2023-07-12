package com.management.model.converter;

import com.management.model.enums.EntityStateEnum;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class EntityStateAttrConverter implements AttributeConverter<EntityStateEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EntityStateEnum attribute) {

        return attribute.getDbCode();
    }

    @Override
    public EntityStateEnum convertToEntityAttribute(Integer dbData) {

        return EntityStateEnum.findStatusByDbCode(dbData);
    }

}
