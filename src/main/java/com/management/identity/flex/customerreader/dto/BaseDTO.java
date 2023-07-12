package com.management.identity.flex.customerreader.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseDTO {

    private String customerNumber;
    private String customerType;
    private String name;
    private String classification;
    private String isFrozen;

}
