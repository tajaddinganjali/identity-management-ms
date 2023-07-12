package com.management.otp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDTO {

    @JsonAlias({"messageID", "message_ID", "message_id"})
    private String messageID;
    private String result;
    @JsonIgnore
    private boolean isLastSms;
    @JsonIgnore
    private String otp;

}
