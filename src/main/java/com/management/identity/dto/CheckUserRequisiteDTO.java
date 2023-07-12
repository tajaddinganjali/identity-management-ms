package com.management.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckUserRequisiteDTO {

    private String pin;
    private String phone;
    private String birthdate;

}
