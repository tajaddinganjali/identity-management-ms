package com.management.config.security.dto;

import com.management.identity.enums.ProfileType;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoFilterRequisiteDTO {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private String token;
    private String userId;
    private String sessionId;
    private String consumerId;
    private String deviceId;
    private String tokenType;
    private ProfileType profileType;

}
