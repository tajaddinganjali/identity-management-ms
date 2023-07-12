package com.management.identity.flex.customerreader.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IdentityDTO {

    private String idNumber;
    private String personalIdType;
    private String issuingDate;
    private String maturityDate;
    private String issuedByAuthority;
    private String pinCode;

}
