package com.management.util;

import com.management.identity.flex.customerreader.dto.DataDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public final class PhoneUtil {

    private PhoneUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String removePhoneNumberCountryCode(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
            String countryCode = "994";
            String operatorCode = "0";
            String plus = "+";
            String plusCountryCode = plus + countryCode;
            if (phoneNumber.startsWith(countryCode)) {
                return phoneNumber.replaceFirst(countryCode, operatorCode);
            } else if (phoneNumber.startsWith(plusCountryCode)) {
                phoneNumber = phoneNumber.replace("+", "");
                return phoneNumber.replaceFirst(countryCode, operatorCode);
            } else if (!phoneNumber.startsWith(operatorCode)) {
                return operatorCode.concat(phoneNumber);
            } else {
                return phoneNumber;
            }
        } else {
            return null;
        }
    }

    public static String removePhoneNumberCountryAndOperatorCode(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.trim();
            String countryCode = "994";
            String operatorCode = "0";
            String plus = "+";
            String plusCountryCode = plus + countryCode;
            if (phoneNumber.startsWith(countryCode)) {
                return phoneNumber.replaceFirst(countryCode, "");
            } else if (phoneNumber.startsWith(plusCountryCode)) {
                phoneNumber = phoneNumber.replace("+", "");
                return phoneNumber.replaceFirst(countryCode, "");
            } else if (phoneNumber.startsWith(operatorCode)) {
                return phoneNumber.replaceFirst(operatorCode, "");
            } else {
                return phoneNumber;
            }
        } else {
            return null;
        }
    }

    public static String addPhoneNumberCountryCodeIntegration(String phoneNumber) {

        String plus = "+";
        String prefix = "994";

        if (phoneNumber.startsWith("0")) {
            return phoneNumber.replaceFirst("0", plus + prefix);
        } else if (phoneNumber.startsWith("994")) {
            return plus.concat(phoneNumber);
        } else if (phoneNumber.startsWith("+994")) {
            return phoneNumber;
        } else if (!phoneNumber.startsWith("0") && !phoneNumber.startsWith("994")
                && !phoneNumber.startsWith("+")) {
            return plus.concat(prefix).concat(phoneNumber);
        } else {
            return null;
        }
    }


    public static String maskNumber(String strText, int start, int end, char maskChar) {

        if (strText == null || strText.equals("")) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }

        if (end > strText.length()) {
            end = strText.length();
        }

        int maskLength = end - start;

        if (maskLength == 0) {
            return strText;
        }

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    public static Map<String, Object> getDataPinPhoneMismatchError(List<String> phoneDTOList) {
        Map<String, Object> phoneMap = new HashMap<>();
        phoneMap.put("phones", phoneDTOList);
        return phoneMap;
    }

    public static List<String> getMaskedPhoneNumbers(List<DataDTO> list) {
        return list.stream()
                .flatMap(customerDetail -> Stream.of(
                        PhoneUtil.removePhoneNumberCountryCode(
                                customerDetail.getPersonal().getContactDetails().getMobile1()),
                        PhoneUtil.removePhoneNumberCountryCode(
                                customerDetail.getPersonal().getContactDetails().getMobile2()),
                        PhoneUtil.removePhoneNumberCountryCode(
                                customerDetail.getPersonal().getContactDetails().getMobile3())))
                .filter(phone -> StringUtils.isNotBlank(phone) && phone.length() == 10)
                .map(number -> PhoneUtil.maskNumber(number, 3, 8, '*'))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getPhoneNumbers(List<DataDTO> list) {
        return list.stream()
                .flatMap(customerDetail -> Stream.of(
                        customerDetail.getPersonal().getContactDetails().getMobile1(),
                        customerDetail.getPersonal().getContactDetails().getMobile2(),
                        customerDetail.getPersonal().getContactDetails().getMobile3()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }


}

