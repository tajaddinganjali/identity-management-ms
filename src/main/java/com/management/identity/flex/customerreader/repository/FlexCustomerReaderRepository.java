package com.management.identity.flex.customerreader.repository;

import com.management.identity.flex.customerreader.config.FlexCustomerReaderFeignConfig;
import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.identity.flex.dto.FlexResponseDTO;
import com.management.identity.flex.enums.FilterType;
import com.management.logging.CustomFeignLogging;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "flexCustomerReaderFeignClient", url = "${flex.customer.reader.url}",
        configuration = {FlexCustomerReaderFeignConfig.class, CustomFeignLogging.class})
public interface FlexCustomerReaderRepository {

    @GetMapping("/customers/detail")
    FlexResponseDTO<List<DataDTO>> getCustomerDetailsByPin(@RequestParam("filter-type") FilterType filterType,
                                                           @RequestParam("filter-value") String filterValue);

}
