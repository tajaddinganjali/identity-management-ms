package com.management.register.service;

import com.management.config.security.UserPrincipial;
import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.dto.SetPasscodeRequestDTO;
import com.management.register.dto.SetPasscodeResponseDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface RegisterService {

    RegisterPinResponseDTO registerWithPin(RegisterRequisiteDTO registerRequisite);

    SetPasscodeResponseDTO setPasscode(UserPrincipial userPrincipial, SetPasscodeRequestDTO setPasscodeRequestDTO,
                                       String authorizationHeader, String lang) throws InvalidKeySpecException, NoSuchAlgorithmException;

}