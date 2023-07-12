package com.management.model;

import com.management.model.enums.UserStatus;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "IDENTITY_USER_AUD")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAud {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "PIN")
    private String pin;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    @Column(name = "IDENTITY_TYPE")
    private String identityType;

    @Column(name = "IDENTITY_NUMBER")
    private String identityNumber;

    @Column(name = "IDENTITY_ISSUING_DATE")
    private String identityIssuingDate;

    @Column(name = "IDENTITY_MATURITY_DATE")
    private String identityMaturityDate;

    @Column(name = "DATE_OF_BIRTH")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "MARITAL_STATUS")
    private String maritalStatus;

    @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    @Column(name = "PASSCODE")
    private String passcode;

    @ManyToOne
    private User user;

    @Column(name = "RESIDENT")
    private Boolean resident;

}
