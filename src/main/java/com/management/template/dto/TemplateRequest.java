package com.management.template.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ApiModel
public class TemplateRequest {

    @NotBlank(message = "template_id must be provided")
    @JsonProperty("template_id")
    private String templateId;
    private Map<String, Object> data;

}
