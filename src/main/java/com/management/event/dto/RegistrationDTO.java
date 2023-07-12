package com.management.event.dto;

import com.management.model.Channel;
import com.management.model.enums.RegistrationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegistrationDTO {

    private String id;
    private String phone;
    private RegistrationStatus status;
    private String consumer;
    private Channel channel;

}
