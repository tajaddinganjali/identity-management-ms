package com.management.template.config;

import com.management.util.ExceptionUtil;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenderClientErrorDecoder implements ErrorDecoder {

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        return ExceptionUtil.handleFeignCommonException(methodKey, response);
    }

}
