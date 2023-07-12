package com.management.event.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CifDTO {

    private String cif;
    private List<PhoneDTO> phones;

}
