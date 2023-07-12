package com.management.model;

import com.management.limit.enums.LimitServiceTypeEnum;
import com.management.model.converter.LimitServiceTypeAttrConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
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
@Table(name = "OTP_LIMIT_CONFIG")
public class OtpLimitConfig extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "otp_limit_config_seq_generator")
    @SequenceGenerator(name = "otp_limit_config_seq_generator", sequenceName = "otp_limit_config_seq",
            allocationSize = 1)
    private long id;

    @Column(name = "fail_round", nullable = false)
    private Integer failRound;

    @Column(name = "per_round_attempt", nullable = false)
    private Integer perRoundAttempt;

    @Column(name = "round_lock_time", nullable = false)
    private String roundLockTime;

    @Column(name = "SERVICE", nullable = false)
    @Convert(converter = LimitServiceTypeAttrConverter.class)
    private LimitServiceTypeEnum service;

}
