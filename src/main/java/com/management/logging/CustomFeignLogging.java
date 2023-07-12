package com.management.logging;

import static feign.Logger.Level.NONE;

import feign.Logger;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

@Slf4j
public class CustomFeignLogging extends Logger {

    public static String getResponseAsText(Response response) {
        try (InputStream inputStream = response.body().asInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            int status = response.status();
            StringJoiner responseLog = new StringJoiner(",");
            responseLog.add(String.format("HTTP Status %s", status));
            String responseText = new String(bytes, StandardCharsets.UTF_8);
            responseLog.add(String.format("Response body : %s", responseText));
            return responseLog.toString();
        } catch (Exception exception) {
            return null;
        }

    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        StringJoiner requestLog = new StringJoiner(",");

        requestLog.add(String.format("HTTP Method: %s", request.httpMethod().name()));
        requestLog.add(String.format(" HTTP Headers: %s", request.headers()));
        requestLog.add(String.format(" HTTP Request uri: %s", request.url()));

        if (logLevel.ordinal() > NONE.ordinal()) {
            requestLog.add(String.format(" Request body : %s", request));
        }
        log.info(requestLog.toString());
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
            throws IOException {

        InputStream inputStream = response.body().asInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);

        try {
            int status = response.status();
            StringJoiner responseLog = new StringJoiner("\n");
            responseLog.add(String.format("HTTP Method: %s", response.request().httpMethod().name()));
            responseLog.add(String.format(" HTTP Status %s", status));

            if (logLevel.ordinal() > NONE.ordinal() || !HttpStatus.valueOf(status).is2xxSuccessful()) {
                String responseText = new String(bytes, StandardCharsets.UTF_8);
                responseLog.add(String.format(" Response body : %s", responseText));
            }
            log.info(responseLog.toString());
        } catch (Exception e) {
            log.error("unable to log response");
        }
        return Response.builder()
                .status(response.status())
                .reason(response.reason())
                .headers(response.headers())
                .request(response.request())
                .body(bytes).build();
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.debug(format(configKey, format, args));
    }

    protected String format(String configKey, String format, Object... args) {
        return String.format(methodTag(configKey) + format, args);
    }

}
