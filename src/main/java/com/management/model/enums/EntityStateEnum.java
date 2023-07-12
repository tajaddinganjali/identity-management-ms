package com.management.model.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;

/**
 * Enum designed to indicate the state of entities.
 */
@Getter
public enum EntityStateEnum {
    /**
     * State that shows that entity has been deleted.
     */
    DELETED(0),
    /**
     * State that shows that current entity is active.
     */
    ACTIVE(1),
    /**
     * State that shows that current entity is updated.
     */
    UPDATED(2),
    /**
     * State that shows that current entity is not active.
     */
    DEACTIVE(3);

    /**
     * Field considered for database code of the entity.
     */
    private final Integer dbCode;

    /**
     * Constructs a new instance with specified database code.
     *
     * @param dbCode code corresponds to the value in database
     */
    EntityStateEnum(int dbCode) {
        this.dbCode = dbCode;
    }

    /**
     * Fetches and returns <code>EntityStateEnum</code> with specified database code.
     *
     * @param dbCode that is required to be searched
     * @return {@code EntityStateEnum} with corresponding code
     */
    public static EntityStateEnum findStatusByDbCode(Integer dbCode) {

        return Arrays.asList(EntityStateEnum.values()).stream()
                .filter(c -> dbCode == c.getDbCode())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No value present"));

    }

}
