package com.management.model;

import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

@Embeddable
@Data
@NoArgsConstructor
public class EmbeddedLimit {

    private String pin;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date firstAttempt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date lastAttempt;

    @Builder
    public EmbeddedLimit(String pin, Date firstAttempt, Date lastAttempt) {
        this.pin = pin;
        this.firstAttempt = firstAttempt;
        this.lastAttempt = lastAttempt;
    }

}
