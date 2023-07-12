package com.management.service;

import com.management.identity.service.impl.UserRegistrationServiceImpl;
import com.management.model.UserRegistration;
import com.management.model.enums.RegistrationStatus;
import com.management.repository.UserRegistrationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceUnitTest {

    @Mock
    private UserRegistrationRepository userRegistrationRepository;
    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    void save() {
        UserRegistration userRegistration = UserRegistration.builder()
                .status(RegistrationStatus.ACTIVE)
                .build();
        Mockito.when(userRegistrationRepository.save(Mockito.any())).thenReturn(UserRegistration.builder().build());
        Assertions.assertDoesNotThrow(() -> userRegistrationService.save(userRegistration));
    }

}
