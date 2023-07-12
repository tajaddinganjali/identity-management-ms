package com.management.register.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLimitAudRequisiteDTO {

    private String pin;
    private String phone;
    private String card;

}
