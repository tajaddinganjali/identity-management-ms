package com.management.otp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsDTO {

    private String title;
    private String text;

}
