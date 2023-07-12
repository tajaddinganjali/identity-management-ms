package com.management.model;

import com.management.register.enums.DeviceStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "IDENTITY_DEVICE")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Device extends Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "DEVICE_STATUS")
    private DeviceStatus deviceStatus;

    @Column(name = "DEVICE_MODEL")
    private String deviceModel;

    @Column(name = "DEVICE_OS")
    private String deviceOs;

    @Column(name = "DEVICE_OS_VERSION")
    private String deviceOsVersion;

    @Column(name = "APP_VERSION")
    private String appVersion;

    @Column(name = "FCM_TOKEN")
    private String fcmToken;

    @Column(name = "ONE_SIGNAL_TOKEN")
    private String oneSignalToken;

    @Column(name = "JAILY_BROKEN")
    private boolean jailyBroken;

    @Column(name = "DEVICE_CODE")
    private String deviceCode;

    @Column(name = "PUSH_ENABLED")
    private boolean pushEnabled;

    @Column(name = "DEVICE_NO")
    private String deviceNo;

    @Column(name = "DEVICE_FINGERPRINT")
    private String deviceFingerprint;

    @Column(name = "LAST_LOGIN_DATE")
    private LocalDateTime lastLoginDate;

    @Column(name = "LAST_ACTIVE_DATE")
    private LocalDateTime lastActiveDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;

}
