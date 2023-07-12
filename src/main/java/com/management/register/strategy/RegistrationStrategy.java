package com.management.register.strategy;

import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.dto.internal.RegisterRequisiteDTO;

public interface RegistrationStrategy {

    RegisterPinResponseDTO registerWithPin(RegisterRequisiteDTO registerRequisite);


}
