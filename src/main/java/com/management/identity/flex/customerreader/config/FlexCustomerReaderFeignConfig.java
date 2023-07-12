package com.management.identity.flex.customerreader.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.identity.flex.customerreader.decoder.FlexCustomerReaderClientErrorDecoder;
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
public class FlexCustomerReaderFeignConfig {

    @Bean(name = "flexCustomerReaderClientDecoder")
    public ErrorDecoder errorDecoder() {
        return new FlexCustomerReaderClientErrorDecoder();
    }

    @Bean("flex_customer_reader_feign_decoder")
    public Decoder feignDecoder() {
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(new MappingJackson2HttpMessageConverter(customObjectMapper()));
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    @Bean("flex_customer_reader_feign_encoder")
    public Encoder feignEncoder() {
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(new MappingJackson2HttpMessageConverter(customObjectMapper()));
        return new SpringFormEncoder(new SpringEncoder(objectFactory));
    }

    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
