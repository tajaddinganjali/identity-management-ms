package com.management.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.management.config.decoder.NotificationFeignClientErrorDecoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class NotificationFeignConfig {

    @Bean(name = "notificationFeignClientDecoder")
    public ErrorDecoder errorDecoder() {
        return new NotificationFeignClientErrorDecoder();
    }

    @Bean("notification_feign_decoder")
    public Decoder feignDecoder() {
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(new MappingJackson2HttpMessageConverter(customObjectMapper()));
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    @Bean("notification_feign_encoder")
    public Encoder feignEncoder() {
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(new MappingJackson2HttpMessageConverter(customObjectMapper()));
        return new SpringFormEncoder(new SpringEncoder(objectFactory));
    }

    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
    }

}
