package com.management.event.dto;

import az.ibar.eventhub.dto.BaseEventDTO;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class UserRegisteredEventDTO extends BaseEventDTO<UserRegisteredDTO> {

}
