package com.management.util;

import com.management.identity.flex.customerreader.dto.DataDTO;
import com.management.model.Customer;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public final class RegistrationUtil {

    private RegistrationUtil() {

    }

    public static List<String> provideUserFlexCifsMatchedByPhone(String requestPhone,
                                                                 List<DataDTO> customerDetailsByPin) {

        String phone = PhoneUtil.removePhoneNumberCountryCode(requestPhone);

        return customerDetailsByPin.stream()
                .filter(filterMatchedByPhone(phone))
                .map(customerDetail -> customerDetail.getBase().getCustomerNumber())
                .collect(Collectors.toList());
    }

    private static Predicate<DataDTO> filterMatchedByPhone(String phone) {
        return customerDetail -> Objects.nonNull(customerDetail.getPersonal())
                && Objects.nonNull(customerDetail.getPersonal().getContactDetails())
                && Objects.nonNull(customerDetail.getBase())
                && StringUtils.isNotBlank(customerDetail.getBase().getCustomerNumber())
                && (Objects.nonNull(customerDetail.getPersonal().getContactDetails().getMobile1())
                && PhoneUtil.removePhoneNumberCountryCode(
                customerDetail.getPersonal().getContactDetails().getMobile1()).equals(phone)
                || Objects.nonNull(customerDetail.getPersonal().getContactDetails().getMobile2())
                && PhoneUtil.removePhoneNumberCountryCode(
                customerDetail.getPersonal().getContactDetails().getMobile2()).equals(phone)
                || Objects.nonNull(customerDetail.getPersonal().getContactDetails().getMobile3())
                && PhoneUtil.removePhoneNumberCountryCode(
                customerDetail.getPersonal().getContactDetails().getMobile3()).equals(phone));
    }

    public static List<String> provideUserFlexCifsMatchedByMobile1(String requestPhone,
                                                                   List<DataDTO> customerDetailsByPin) {
        String phone = PhoneUtil.removePhoneNumberCountryCode(requestPhone);
        return customerDetailsByPin.stream()
                .filter(filterMatchedByMobile1(phone))
                .map(customerDetail -> customerDetail.getBase().getCustomerNumber())
                .collect(Collectors.toList());
    }

    private static Predicate<DataDTO> filterMatchedByMobile1(String phone) {
        return customerDetail -> Objects.nonNull(customerDetail.getPersonal())
                && Objects.nonNull(customerDetail.getPersonal().getContactDetails())
                && Objects.nonNull(customerDetail.getBase())
                && StringUtils.isNotBlank(customerDetail.getBase().getCustomerNumber())
                && Objects.nonNull(customerDetail.getPersonal().getContactDetails().getMobile1())
                && PhoneUtil.removePhoneNumberCountryCode(
                customerDetail.getPersonal().getContactDetails().getMobile1()).equals(phone);
    }

    public static Customer convertIntegration(DataDTO dataDTO) {

        //Business logic
        return Customer.builder().build();

    }

}

