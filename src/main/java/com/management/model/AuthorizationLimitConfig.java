package com.management.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name = "AUTHORIZATION_LIMIT_CONFIG")
public class AuthorizationLimitConfig extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "authorization_limit_config_seq_generator")
    @SequenceGenerator(name = "authorization_limit_config_seq_generator",
            sequenceName = "authorization_limit_config_seq", allocationSize = 1)
    private long id;

    @Embedded
    @AttributeOverride(name = "attemptCount", column = @Column(name = "ATTEMPT_COUNT", nullable = false))
    @AttributeOverride(name = "lockTime", column = @Column(name = "LOCK_TIME", nullable = false))
    @AttributeOverride(name = "service", column = @Column(name = "SERVICE", nullable = false))
    private EmbeddedLimitConfig embeddedLimitConfig;

}
