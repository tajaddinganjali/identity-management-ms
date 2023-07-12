package com.management.model;

import com.management.model.config.ModelListener;
import com.management.model.converter.EntityStateAttrConverter;
import com.management.model.enums.EntityStateEnum;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * This abstract class is designed to be a boilerplate for all entities used in this microservice,
 * It contains 3 fields that are considered to be included in all entities. These fields include
 * creation date, modification date, and state of the entity.
 *
 * <p>Additionally, it uses entity listener to update those fields whenever needed.
 *
 * @see ModelListener
 */
@MappedSuperclass
@Data
@EntityListeners(ModelListener.class)
public class AbstractEntity {

    /**
     * Date that entity was created.
     */
    @Column(name = "CREATED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createdDate;

    /**
     * Date that entity was updated.
     */
    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date updatedDate;

    /**
     * State that shows the current state of the entity.
     * Values of this field are related to <code>EntityStateEnum</code>.
     *
     * @see EntityStateEnum
     */
    @Column(nullable = false)
    @Convert(converter = EntityStateAttrConverter.class)
    private EntityStateEnum state;

}
