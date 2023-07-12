package com.management.model;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "REGISTER_LIMIT_AUD")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterLimitAud extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "PIN")
    private String pin;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "CARD")
    private String card;

}
