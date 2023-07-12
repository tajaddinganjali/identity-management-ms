package com.management.register.service;

import com.management.register.enums.ServiceName;

public interface RequestLogService {

    void logRequestToDb(Object requestObject, ServiceName serviceName, String exceptionName);

}
