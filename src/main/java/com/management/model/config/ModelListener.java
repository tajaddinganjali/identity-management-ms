package com.management.model.config;


import com.management.model.AbstractEntity;
import com.management.model.enums.EntityStateEnum;
import com.management.util.TimeUtil;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Listener solely designed to manage time-based information of
 * all entities extended from the <code>AbstractEntity</code>.
 *
 * @see AbstractEntity
 */
public class ModelListener {

    /**
     * Sets creation date and state of entities, before entities being inserted into a database.
     *
     * @param abstractEntity entity that is required to be set
     * @see com.management.model.AbstractEntity
     */
    @PrePersist
    public void onPersist(AbstractEntity abstractEntity) {
        abstractEntity.setCreatedDate(TimeUtil.getCurrentDateTime());
        abstractEntity.setState(EntityStateEnum.ACTIVE);
    }

    /**
     * Updates creation date and state of entities, before entities being updated in database.
     *
     * @param abstractEntity entity that is required to be set
     * @see AbstractEntity
     */
    @PreUpdate
    public void onUpdate(AbstractEntity abstractEntity) {
        abstractEntity.setState(EntityStateEnum.UPDATED);
        abstractEntity.setUpdatedDate(TimeUtil.getCurrentDateTime());
    }

}
