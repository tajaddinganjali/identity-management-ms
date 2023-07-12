package com.management.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

/**
 * Extension of <code>AbstractEntity</code> especially designed to handle category related data.
 *
 * @see AbstractEntity
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Where(clause = "state <> 0 AND state <> 3")
@Table(name = "AUTHORIZATION_LIMIT")
public class AuthorizationLimit extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "authorization_limit_seq_generator")
    @SequenceGenerator(name = "authorization_limit_seq_generator", sequenceName = "authorization_limit_seq", allocationSize = 1)
    private long id;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    private Integer attemptCount;

    @Embedded
    @AttributeOverride(name = "pin", column = @Column(name = "PIN"))
    @AttributeOverride(name = "firstAttempt", column = @Column(name = "FIRST_ATTEMPT"))
    @AttributeOverride(name = "lastAttempt", column = @Column(name = "LAST_ATTEMPT"))
    private EmbeddedLimit embeddedLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIG_ID", nullable = false)
    private AuthorizationLimitConfig authorizationLimitConfig;

}
