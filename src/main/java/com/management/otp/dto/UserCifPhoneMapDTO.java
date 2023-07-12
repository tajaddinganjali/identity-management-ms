package com.management.otp.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
public class UserCifPhoneMapDTO implements Serializable {

    private static final long serialVersionUID = 123456789L;

    private Map<String, List<String>> userPhoneCifs;
    private boolean isPreviousCifsMustBeDeleted;

}
