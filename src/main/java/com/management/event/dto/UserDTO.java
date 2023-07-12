package com.management.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.management.identity.enums.ProfileType;
import com.management.register.dto.internal.ReferralInfoDTO;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserDTO {

    private String id;
    private String pin;
    private String phone;
    private String name;
    private String surname;
    private String cif;
    private String cardNumber;
    private List<CifDTO> cifs;
    private List<RegistrationDTO> registrations;
    private String gender;
    private String maritalStatus;
    private boolean resident;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private OffsetDateTime registrationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    private Integer age;
    private ProfileType profileType;
    private String language;
    private ReferralInfoDTO referral;

}
