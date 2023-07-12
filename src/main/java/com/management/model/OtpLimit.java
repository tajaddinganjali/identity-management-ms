package com.management.model;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "OTP_LIMIT")
public class OtpLimit extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "otp_limit_seq_generator")
    @SequenceGenerator(name = "otp_limit_seq_generator", sequenceName = "otp_limit_seq", allocationSize = 1)
    private long id;

    @Column(name = "fail_round", nullable = false)
    private Integer failRound;

    @Column(name = "per_round_attempt", nullable = false)
    private Integer perRoundAttempt;

    @Column(name = "LOCKED_TO", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date lockedTo;

    @Embedded
    @AttributeOverride(name = "pin", column = @Column(name = "PIN", nullable = false))
    @AttributeOverride(name = "firstAttempt", column = @Column(name = "FIRST_ATTEMPT"))
    @AttributeOverride(name = "lastAttempt", column = @Column(name = "LAST_ATTEMPT"))
    private EmbeddedLimit embeddedLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIG_ID", nullable = false)
    private OtpLimitConfig otpLimitConfig;

}
