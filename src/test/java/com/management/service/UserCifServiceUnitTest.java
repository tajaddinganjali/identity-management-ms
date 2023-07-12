package com.management.service;

import com.management.identity.service.impl.UserCifServiceImpl;
import com.management.model.UserCif;
import com.management.repository.UserCifRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCifServiceUnitTest {

    @Mock
    private UserCifRepository userCifRepository;
    @InjectMocks
    private UserCifServiceImpl userCifService;

    @Test
    void getCifsByPhone() {
        UserCif userCif = UserCif.builder()
                .cif("cif")
                .build();
        Mockito.when(userCifRepository.getCifsByPhone(Mockito.anyString())).thenReturn(List.of(userCif));
        Assertions.assertDoesNotThrow(() -> userCifService.getCifsByPhone("phone"));
    }

}
