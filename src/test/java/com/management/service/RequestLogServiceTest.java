package com.management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.model.RequestLog;
import com.management.register.enums.ServiceName;
import com.management.register.service.impl.RequestLogServiceImpl;
import com.management.repository.RequestLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestLogServiceTest {

    @Mock
    private RequestLogRepository requestLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RequestLogServiceImpl requestLogService;

    @BeforeEach
    public void setup() {
        requestLogService = new RequestLogServiceImpl(requestLogRepository, objectMapper);
    }

    @Test
    void logRequestToDb() throws JsonProcessingException {

        RequestLog requestLog = RequestLog.builder()
                .request("jsonRequest")
                .serviceName(ServiceName.REGISTER_BY_PIN)
                .exceptionName("exceptionName")
                .build();
        Mockito.when(requestLogRepository.save(Mockito.any())).thenReturn(requestLog);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any()))
                .thenThrow(JsonProcessingException.class);
    }

}
