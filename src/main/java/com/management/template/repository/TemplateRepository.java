package com.management.template.repository;


import com.management.logging.CustomFeignLogging;
import com.management.template.config.RenderClientConfig;
import com.management.template.dto.TemplateRequest;
import com.management.template.dto.TemplateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "template",
        url = "${render.service.url}",
        configuration = {RenderClientConfig.class, CustomFeignLogging.class})
public interface TemplateRepository {

    @PostMapping("render")
    TemplateResponse getRenderedData(@RequestBody TemplateRequest templateRequest);

}
