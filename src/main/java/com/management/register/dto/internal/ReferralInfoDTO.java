package com.management.register.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.management.register.enums.ReferralType;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ReferralInfoDTO implements Serializable {

    @JsonProperty("referral_type")
    private ReferralType referralType;

    @JsonProperty("referred_by")
    private String referredBy;

}
