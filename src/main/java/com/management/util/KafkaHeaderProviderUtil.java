package com.management.util;

import az.ibar.eventhub.dto.HeaderDTO;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaHeaderProviderUtil {

    public static HeaderDTO getHeader(String eventId, String eventType, String consumerId) {
        return HeaderDTO.builder()
                .eventId(eventId)
                .eventType(eventType)
                .version("v1")
                .originatorConsumer(consumerId)
                .originatorSystem("identity")
                .originatorTraceId(UUID.randomUUID().toString())
                .occurredAt(OffsetDateTime.now())
                .build();
    }

}
