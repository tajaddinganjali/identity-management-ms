package com.management.register.enums;

public enum KafkaEventType {
    REGISTERED_BY_PIN("UserRegisteredByPin"),
    REGISTERED_BY_CARD("UserRegisteredByCard"),
    LANGUAGE_CHANGED("UserLanguageChanged");

    private final String eventType;

    KafkaEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
