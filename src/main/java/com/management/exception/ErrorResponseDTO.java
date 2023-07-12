package com.management.exception;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    private int status;
    private String code;
    @JsonAlias({"error_code", "errorCode"})
    private String errorCode;
    private String message;
    private String detail;
    @JsonProperty("help_url")
    private String helpUrl;
    private String path;
    @JsonProperty("requested_lang")
    private String requestedLang;
    @JsonProperty("provided_lang")
    private String providedLang;

}
