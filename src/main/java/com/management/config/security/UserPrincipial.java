package com.management.config.security;

import com.management.identity.enums.ProfileType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class UserPrincipial {

    @ApiModelProperty(hidden = true)
    private String userId;

    @ApiModelProperty(hidden = true)
    private String sessionId;

    @ApiModelProperty(hidden = true)
    private String consumerId;

    @ApiModelProperty(hidden = true)
    private ProfileType profileType;

    @ApiModelProperty(hidden = true)
    private String deviceId;

    @ApiModelProperty(hidden = true)
    private String tokenType;

}
