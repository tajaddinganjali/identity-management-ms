package com.management.identity.service;

import com.management.identity.dto.CheckUserRequisiteDTO;
import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.model.Consumer;
import com.management.model.User;
import com.management.register.dto.RegisterCacheDTO;
import com.management.register.enums.RegisterCaseTypeEnum;
import java.util.List;

public interface IdentityService {

    RegisterCaseTypeEnum identifyUser(CheckUserRequisiteDTO checkUserRequisite);

    FlexResponseDTO<List<DataDTO>> getFlexCustomer(String pin);

    User createDBUser(RegisterCacheDTO registerCache);

    void checkDuplicatePhone(String phone, List<String> userFlexCifsMatchedByPhone,
                             User userFormedFromFlex, Consumer consumer);

    Consumer findConsumerById(String consumerId);

}
