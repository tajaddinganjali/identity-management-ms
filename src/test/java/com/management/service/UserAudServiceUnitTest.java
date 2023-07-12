package com.management.service;

import com.management.identity.service.impl.UserAudServiceImpl;
import com.management.model.User;
import com.management.model.UserAud;
import com.management.model.enums.UserStatus;
import com.management.repository.UserAudRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAudServiceUnitTest {

    @Mock
    private UserAudRepository userAudRepository;
    @InjectMocks
    private UserAudServiceImpl userAudService;

    @Test
    void save() {
        Mockito.when(userAudRepository.save(Mockito.any())).thenReturn(UserAud.builder().build());
        Assertions.assertDoesNotThrow(() -> userAudService.save(getUser()));
    }

    private User getUser() {
        return User.builder()
                .cardNumber("card")
                .cif("cif")
                .gender("gender")
                .identityIssuingDate("date")
                .identityMaturityDate("date")
                .identityNumber("number")
                .identityIssuingDate("type")
                .maritalStatus("status")
                .dateOfBirth(Date.valueOf(LocalDate.now()))
                .name("name")
                .passcode("code")
                .phone("phone")
                .pin("pin")
                .status(UserStatus.ACTIVE)
                .version(1L)
                .surname("surname")
                .updatedAt(OffsetDateTime.now())
                .resident(true)
                .build();
    }

}
