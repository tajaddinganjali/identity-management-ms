package com.management.service;

import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.customerreader.repository.FlexCustomerReaderRepository;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.flex.service.impl.FlexServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexServiceUnitTest {

    @Mock
    private FlexCustomerReaderRepository flexCustomerReaderRepository;
    @Mock
    private FlexResponseDTO<List<DataDTO>> flexResponseDTOMono;
    @InjectMocks
    private FlexServiceImpl flexService;

    @Test
    void getUserByPinPhone() {
        Mockito.when(flexCustomerReaderRepository.getCustomerDetailsByPin(Mockito.any(), Mockito.anyString()))
                .thenReturn(flexResponseDTOMono);
        Assertions.assertDoesNotThrow(() -> flexService.getCustomer("pin"));
    }

}
