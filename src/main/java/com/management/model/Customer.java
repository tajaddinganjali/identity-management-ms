package com.management.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerNumber;
    private String customerType;
    private String name;
    private String firstName;
    private String lastName;
    private String fatherName;
    private Date dateOfBirth;
    private String residentStatus;
    private String maritalStatus;
    private String gender;
    private String pin;
    private Integer age;
    private String classification;

}
