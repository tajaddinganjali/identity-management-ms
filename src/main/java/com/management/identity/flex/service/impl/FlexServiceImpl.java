package com.management.identity.flex.service.impl;

import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.customerreader.repository.FlexCustomerReaderRepository;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.flex.enums.FilterType;
import com.management.identity.flex.service.FlexService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlexServiceImpl implements FlexService {

    private final FlexCustomerReaderRepository flexCustomerReaderRepository;


    @Override
    @Cacheable("flexCustomerReaderByPin")
    public FlexResponseDTO<List<DataDTO>> getCustomer(String pin) {
        return flexCustomerReaderRepository.getCustomerDetailsByPin(FilterType.PIN, pin);
    }

}
