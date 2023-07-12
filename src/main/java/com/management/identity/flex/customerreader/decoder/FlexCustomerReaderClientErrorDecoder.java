package com.management.identity.flex.customerreader.decoder;

import com.management.exception.BadRequestException;
import com.management.exception.NotFoundErrorEnum;
import com.management.util.ExceptionUtil;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FlexCustomerReaderClientErrorDecoder implements ErrorDecoder {

    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {

        int responseStatus = response.status();
        if (HttpStatus.resolve(responseStatus) == HttpStatus.NOT_FOUND) {
            throw new BadRequestException(NotFoundErrorEnum.FLEX_USER_NOT_FOUND, "not found");
        }
        return ExceptionUtil.handleFeignCommonException(methodKey, response);
    }

}
