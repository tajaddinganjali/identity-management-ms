package com.management.model;

import com.management.model.enums.SessionStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "IDENTITY_SESSION")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column
    private String deviceCode;

    @Column
    private SessionStatus status;

    @CreationTimestamp
    @Column
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime closedAt;

    @Column
    private String authorizationCode;

    @Column(length = 512)
    private String authorizationToken;

    @Column
    private String consumer;

    @Column(length = 1024)
    private String accessToken;

    @Column(length = 1024)
    private String refreshToken;

    @OneToOne(targetEntity = User.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private Device device;

}
