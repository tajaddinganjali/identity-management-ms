package com.management.otp.config;

import com.management.exception.GeneralTechnicalException;
import com.management.util.ExceptionUtil;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
@RequiredArgsConstructor
public class SMSFeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String responseBody = response.body() != null ? IOUtils.toString(response.body().asInputStream(),
                    StandardCharsets.UTF_8.name()) : null;
            return ExceptionUtil.handleOtpException(methodKey, response.status(), responseBody);
        } catch (IOException e) {
            log.error("error occurred when read exception value. exception message: {}",
                    e.getMessage());
            throw new GeneralTechnicalException(e.getMessage());
        }
    }

}
