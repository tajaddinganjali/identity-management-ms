package com.management.otp.repository;

import com.management.logging.CustomFeignLogging;
import com.management.otp.config.SMSFeignClientConfig;
import com.management.otp.dto.OTPRequestDTO;
import com.management.otp.dto.OTPResponseDTO;
import com.management.otp.enums.OtpLimitTypeEnum;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "sms",
        url = "${sms.url}",
        configuration = {SMSFeignClientConfig.class, CustomFeignLogging.class})
public interface OtpRepository {

    @PostMapping("/sms")
    OTPResponseDTO sendSMS(@RequestBody OTPRequestDTO request);

    @GetMapping("/sms/{limit-type}/{phone}")
    void checkSmsLimit(
            @PathVariable("limit-type") OtpLimitTypeEnum limitServiceType,
            @PathVariable("phone") String phone);

}
