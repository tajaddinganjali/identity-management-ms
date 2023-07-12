package com.management.model;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

@MappedSuperclass
@Getter
public class Base {

    @CreationTimestamp
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    @Audited
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Audited
    private OffsetDateTime updatedAt;

}
