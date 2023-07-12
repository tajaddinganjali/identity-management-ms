package com.management.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.management.model.enums.RegistrationStatus;
import com.management.model.enums.VerificationMethod;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;


@Entity
@Table(name = "IDENTITY_USER_REGISTRATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
public class UserRegistration extends BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-registration")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;

    @Column(name = "CONSUMER")
    private String consumer;

    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private RegistrationStatus status;

    @Column(name = "CHANNEL")
    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Column(name = "VERIFICATION_METHOD")
    @Enumerated(EnumType.STRING)
    private VerificationMethod verificationMethod;

}
