package com.management.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.management.identity.enums.ProfileType;
import com.management.model.enums.UserStatus;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "IDENTITY_USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "CIF")
    private String cif;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonManagedReference("user-cif")
    private List<UserCif> cifs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JsonManagedReference("user-registration")
    private List<UserRegistration> registrations;

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

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    @Column(name = "PASSCODE")
    private String passcode;

    @Column(name = "RESIDENT")
    private Boolean resident;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "PROFILE_TYPE")
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;

    @Column(name = "LANGUAGE", nullable = true)
    private String language;

    @Column(name = "CLASSIFICATION")
    private String classification;

    @Column(name = "DW_CUSTOMER_ID")
    private String dwCustomerId;

    @Column(name = "DW_ACCOUNT_ID")
    private String dwAccountId;

    @Column(name = "DW_ACCOUNT_NO")
    private String dwAccountNo;

    @Column(name = "EMAIL")
    private String email;

}
