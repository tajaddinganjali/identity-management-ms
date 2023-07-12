package com.management.identity.flex.customerreader.dto;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonalDTO {

    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private Integer age;
    private String sex;
    private IdentityDTO id;
    private ContactDetailsDTO contactDetails;
    private String residentStatus;
    private String educationalStatus;
    private String maritalStatus;
    private String nationality;

    public Integer getAge() {
        if (Objects.isNull(age)) {
            if (Objects.nonNull(dateOfBirth)) {
                LocalDate birthDate = LocalDate.parse(dateOfBirth);
                age = Period.between(birthDate, LocalDate.now()).getYears();
                return age;
            } else {
                return age;
            }
        } else {
            return age;
        }
    }

}
