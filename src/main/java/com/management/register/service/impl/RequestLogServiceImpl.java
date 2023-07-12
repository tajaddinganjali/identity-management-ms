package com.management.register.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.model.RequestLog;
import com.management.register.enums.ServiceName;
import com.management.register.service.RequestLogService;
import com.management.repository.RequestLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogRepository requestLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void logRequestToDb(Object requestObject, ServiceName serviceName, String exceptionName) {

        String jsonRequest = null;
        try {
            jsonRequest = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        RequestLog requestLog = RequestLog.builder()
                .request(jsonRequest)
                .serviceName(serviceName)
                .exceptionName(exceptionName)
                .build();

        requestLogRepository.save(requestLog);
    }

}
