package com.management.event.publisher;

import com.management.event.UserRegistrationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUserRegistrationCompletedEvent(final UserRegistrationCompletedEvent user) {
        log.info("publishing user registration completed event ...");
        publisher.publishEvent(user);
    }

}
