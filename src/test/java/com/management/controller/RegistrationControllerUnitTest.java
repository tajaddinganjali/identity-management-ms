package com.management.controller;

import com.management.register.controller.RegistrationController;
import com.management.register.dto.RegisterPinRequestDTO;
import com.management.register.dto.RegisterPinResponseDTO;
import com.management.register.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerUnitTest {

    @Mock
    private RegisterService registerService;
    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void registerWithPin() {
        Mockito.when(registerService.registerWithPin(Mockito.any())).thenReturn(RegisterPinResponseDTO.builder().build());
        Assertions.assertDoesNotThrow(() -> registrationController.registerWithPin("lang", "version", RegisterPinRequestDTO.builder().build()));
    }

}
