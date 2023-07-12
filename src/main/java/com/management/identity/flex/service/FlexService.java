package com.management.identity.flex.service;

import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import java.util.List;

public interface FlexService {

    FlexResponseDTO<List<DataDTO>> getCustomer(String pin);

}
