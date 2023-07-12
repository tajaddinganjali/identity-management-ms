package com.management.event;

import com.management.model.Device;
import com.management.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationCompletedEvent {

    private User user;
    private String consumerId;
    private Device device;
    private boolean isFirstRegistration;

}
