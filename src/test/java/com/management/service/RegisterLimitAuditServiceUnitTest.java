package com.management.service;

import com.management.model.RegisterLimitAud;
import com.management.register.service.impl.RegisterLimitAuditServiceImpl;
import com.management.repository.RegisterLimitAuditRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterLimitAuditServiceUnitTest {

    @Mock
    private RegisterLimitAuditRepository registerLimitAuditRepository;
    @InjectMocks
    private RegisterLimitAuditServiceImpl registerLimitAuditService;

    @Test
    void save() {
        Mockito.when(registerLimitAuditRepository.save(Mockito.any())).thenReturn(RegisterLimitAud.builder().build());
        Assertions.assertDoesNotThrow(() -> registerLimitAuditService.save(RegisterLimitAud.builder()
                .pin("1234PG6")
                .phone("+994555555555")
                .build()));
    }


}
