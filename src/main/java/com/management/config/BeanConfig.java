package com.management.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.management.model.User;
import com.management.util.HmacOTP;
import com.management.util.TimeBasedOTP;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    private final ObjectMapper objectMapper;

    @Autowired
    public BeanConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Bean("customJacksonCodec")
    public TypedJsonJacksonCodec getCodec() {
        return new TypedJsonJacksonCodec(new TypeReference<String>() {
        }, new TypeReference<User>() {
        }, objectMapper);
    }

    @Bean("customTimeBasedOtp")
    public TimeBasedOTP timeBasedOTP() {
        return new TimeBasedOTP(HmacOTP.HMAC_SHA256, 6, 120, 1);
    }

}
