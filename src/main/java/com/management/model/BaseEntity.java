package com.management.model;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

@MappedSuperclass
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class BaseEntity {

    @CreationTimestamp
    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "VERSION")
    private Long version;

}
