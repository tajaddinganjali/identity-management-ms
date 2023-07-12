package com.management.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserRegisteredDTO {

    private String id;
    private UserDTO user;
    private DeviceDTO device;
    private boolean newRegister;

}

